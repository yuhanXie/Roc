package com.roc.code.contain;

/**
 * @author gang.xie
 */
public class L710 {

    public static void main(String[] args) {
        L710 l710 = new L710();
        // 5 2 6
        // n n -1 n - 2
        System.out.println(l710.numSubarrayProductLessThanKV2(new int[]{10, 5, 2, 6}, 100));
//        System.out.println(l710.numSubarrayProductLessThanKV2(new int[]{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
//                1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, 100));
//        System.out.println(l710.numSubarrayProductLessThanKV2(new int[]{1, 2, 3}, 0));
    }


    /**
     * L713
     * <p>
     * 1 <= nums.length <= 3 * 104
     * 1 <= nums[i] <= 1000
     * 0 <= k <= 106
     *
     * @param nums
     * @param k
     * @return
     */
    public int numSubarrayProductLessThanK(int[] nums, int k) {
        //使用暴力法,深度优先
        for (int i = 0; i < nums.length; i++) {
            dfs(i, nums[i], k, nums);
        }
        return result;

    }

    private int result = 0;

    private void dfs(int i, int pre, int k, int[] nums) {
        if (pre >= k) {
            return;
        }

        result++;
        if (i + 1 < nums.length) {
            dfs(i + 1, pre * nums[i + 1], k, nums);
        }
    }


    public int numSubarrayProductLessThanKV2(int[] nums, int k) {

        if (nums.length < 2) {
            return nums[0] >= k ? 0 : 1;
        }
        int result = 0;
        int leftIndex = 0;
        int rightIndex = 0;
        int pre = 1;
        while (leftIndex <= rightIndex && rightIndex < nums.length) {
            pre *= nums[rightIndex];
            while (pre >= k) {
                pre /= nums[leftIndex];
                leftIndex++;
            }
            rightIndex++;
            result += rightIndex - leftIndex;
        }

        return result;
    }


}

package com.roc.code.contain;

import java.util.Arrays;

/**
 * @author gang.xie
 */
public class L900 {

    public static void main(String[] args) {
        L900 l900 = new L900();
//        int[] result = l900.sortArrayByParity(new int[]{1, 3, 2, 4});
//        int[] result = l900.sortArrayByParity(new int[]{2, 3, 1, 4});
//        int[] result = l900.sortArrayByParity(new int[]{1, 2, 3, 4});
//        int[] result = l900.sortArrayByParity(new int[]{2, 4, 1, 3});
        int[] result = l900.sortArrayByParity(new int[]{0});
        Arrays.stream(result).forEach(System.out::println);
    }

    /**
     * L905 按奇偶排序数组
     * 给你一个整数数组 nums，将 nums 中的的所有偶数元素移动到数组的前面，后跟所有奇数元素。
     *
     * @param nums
     * @return
     */
    public int[] sortArrayByParity(int[] nums) {
        // 1 3 2 4
        // 2 3 1 4
        // 1 3 2 4
        // 2 4 1 3
        int n = nums.length;
        int leftIndex = 0;
        int rightIndex = n - 1;
        while (leftIndex < rightIndex) {
            while (leftIndex < n && nums[leftIndex] % 2 == 0) {
                leftIndex++;
            }
            while (rightIndex >= 0 && nums[rightIndex] % 2 != 0) {
                rightIndex--;
            }

            if (leftIndex < rightIndex) {
                int temp = nums[leftIndex];
                nums[leftIndex] = nums[rightIndex];
                nums[rightIndex] = temp;
                leftIndex++;
                rightIndex--;
            }
        }

        return nums;
    }
}

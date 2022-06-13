package com.roc.code.l0;

/**
 * @author xiegang
 */
public class L40 {


    public static void main(String[] args) {
        L40 l40 = new L40();
        System.out.println(l40.trap(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}));
        System.out.println(l40.trap(new int[]{4, 2, 0, 3, 2, 5}));
    }

    /**
     * L41
     *
     * @param nums
     * @return
     */
    //数字可能是负数
    //数字可能重复
    //7891
    public int firstMissingPositive(int[] nums) {
        //数字交换
        int i = 0;
        while (i < nums.length) {
            int cur = nums[i];
            if (cur <= 0 || cur > nums.length || cur == i + 1 || nums[cur - 1] == cur) {
                //负数，大于数组长度，已在正确位置，正确位置上已有正确数据
                i++;
            } else {
                nums[i] = nums[cur - 1];
                nums[cur - 1] = cur;
            }
        }

        for (int j = 0; j < nums.length; j++) {
            if (nums[j] != j + 1) {
                return j + 1;
            }
        }
        //当数组数据是12345时，要输出6
        return nums.length + 1;
    }

    /**
     * L42. 接雨水
     * 给定 n 个非负整数表示每个宽度为 1 的柱子的高度图，计算按此排列的柱子，下雨之后能接多少雨水。
     * n == height.length
     * 1 <= n <= 2 * 104
     * 0 <= height[i] <= 105
     *
     * @param height
     * @return
     */
    public int trap(int[] height) {
        //0,1,0,2,1,0,1,3,2,1,2,1
        //思路其实是不太好想出来的，建议直接题解，题解告诉我们将问题拆分
        //拆分为求每个柱子可以接的雨水的数量
        //那每个柱子可以接的水量怎么求呢？当前柱子左右两边的最高柱子较低值 - 当前柱子的高度
        // （当前柱子如果高于左右两边最高柱子的较低值，那么这个柱子无法接雨水）
        // 1. 先求每个柱子左右两边的最高值
        // 2. for循环计算即可
        int n = height.length;
        //至少需要三根柱子
        if (n < 3) {
            return 0;
        }
        int[] left = new int[n];
        int[] right = new int[n];
        for (int i = 1; i < n; i++) {
            left[i] = Math.max(left[i - 1], height[i - 1]);
        }
        for (int i = n - 2; i >= 0; i--) {
            right[i] = Math.max(right[i + 1], height[i + 1]);
        }
        int sum = 0;
        //头和尾都没有办法接雨水
        for (int i = 1; i < n - 1; i++) {
            int min = Math.min(left[i], right[i]);
            sum += min > height[i] ? min - height[i] : 0;
        }
        return sum;
    }
}

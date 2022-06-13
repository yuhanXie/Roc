package com.roc.code;

/**
 * @author gang.xie
 */
public class Test {

    public static void main(String[] args) {
        int[] test1 = new int[]{-1, 5, 3, 2, 1};
        int[] test2 = new int[]{1, 5, 3, 2, 1};
        int[] test3 = new int[]{7, 8, 9, 11, 12};
        int[] test4 = new int[]{1};
        Test test = new Test();
        System.out.println(test.firstMissingPositive(test1));
        System.out.println(test.firstMissingPositive(test2));
        System.out.println(test.firstMissingPositive(test3));
        System.out.println(test.firstMissingPositive(test4));

    }

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
}

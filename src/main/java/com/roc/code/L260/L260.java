package com.roc.code.L260;

import java.util.Arrays;

/**
 * @author xiegang
 */
public class L260 {

    public static void main(String[] args) {
        L260 l260 = new L260();
//        int[] result = l260.singleNumber(new int[]{1, 2, 3, 1, 5, 2});
        int[] result = l260.singleNumber(new int[]{2, 1, 2, 3, 4, 1});
        Arrays.stream(result).forEach(System.out::println);
    }

    /**
     * L260. 只出现一次的数字 III
     * <p>
     * 给定一个整数数组 nums，其中恰好有两个元素只出现一次，其余所有元素均出现两次。
     * 找出只出现一次的那两个元素。
     * 要求时间复杂度O(n)， 空间复杂度O(1)
     * <p>
     * 11 5 3 22
     *
     * @param nums
     * @return
     */
    public int[] singleNumber(int[] nums) {
        //异或 相同是0， 不同是1
        //1. 将数组所有元素异或-> 结果为两个不相同元素m，n的异或值,设为x
        //2. y = x & -x, y就是取x的最低位1的值，也就是m，n从低往高位第一个不同的位
        // m = 3, n = 5 -> x = 6 -> y = 4, m:011, n:101, y:100
        //3. 将数组分不同的m，n最低位为1和不为1的两个数组，
        // 相同的数字肯定在一个组，同时又将m，n分开了。就可以用异或找到两个不同的元素
        int x = 0;
        for (int num : nums) {
            x ^= num;
        }
        int y = x & -x;
        int[] result = new int[2];
        for (int num : nums) {
            if ((num & y) == 0) {
                result[0] ^= num;
            } else {
                result[1] ^= num;
            }
        }
        return result;
    }


    /**
     * L268
     *
     * @param nums
     * @return
     */
    //时间：O(n)， O(n)
    public int missingNumber(int[] nums) {
//输入：nums = [9,6,4,2,3,5,7,0,1]
//输出：8
//解释：n = 9，因为有 9 个数字，所以所有的数字都在范围 [0,9] 内。8 是丢失的数字，因为它没有出现在 nums 中。

        int[] newNums = new int[nums.length + 1];
        for (int num : nums) {
            newNums[num] = 1;
        }
        for (int i = 0; i < newNums.length; i++) {
            if (newNums[i] == 0) {
                return i;
            }
        }
        return -1;
    }

    public int missingNumberV2(int[] nums) {
        //异或是相同为0，不同是1。把所有的值和index进行异或，
        //存在的值和对应的index异或之后都是0，缺失对应的index
        int num = nums.length;
        for (int i = 0; i < nums.length; i++) {
            num ^= i ^ nums[i];
        }

        return num;

    }


}

package com.roc.code.contain;

/**
 * @author xiegang
 */
public class L360 {


    /**
     * 367. 有效的完全平方数
     *
     * 给定一个 正整数 num ，编写一个函数，如果 num 是一个完全平方数，则返回 true ，否则返回 false 。
     *
     * 进阶：不要 使用任何内置的库函数，如  sqrt 。
     *
     * @param num
     * @return
     */
    public boolean isPerfectSquare(int num) {
        //暴力法:找小于num的元素，相乘，若等于num，那么就是完全平方数
        //很不幸，超出时间限制了
        if (num == 1) {
            return true;
        }
        for (int i = 1; i <= num/2; i++) {
            if (i * i == num) {
                return true;
            }
        }
        return false;
    }

    public boolean isPerfectSquareV2(int num) {
        //找到一个规律，从1开始，完全平方数是增加3，5，7，9...
        //遍历试试看
        //同样超出时间限制。。。感觉是不能用递进的方式
        int temp = 1;
        int add = 3;
        while (temp < num) {
            temp += add;
            add += 2;
        }
        return temp == num;
    }

    public boolean isPerfectSquareV3(int num) {
        //用递进的方式一定会超出时间限制,
        // 看规律，除了1，4，完全平方数的因子都是大于num/2,那么用二分来试试，会减少递进次数
        //盲写二分查找之后，需要验证下边界值，我经常边界值考虑不清楚
        int left = 1;
        int right = num;
        while (left <= right) {
            int mid = (left + right) >> 1;
            long temp = (long) mid * mid;
            if (temp == num) {
                return true;
            }
            if (temp > num) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return false;
    }
}

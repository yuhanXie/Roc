package com.roc.code.l0;

/**
 * @author xiegang
 */
public class L70 {


    /**
     * L70. 爬楼梯
     * <p>
     * 假设你正在爬楼梯。需要 n 阶你才能到达楼顶。
     * <p>
     * 每次你可以爬 1 或 2 个台阶。你有多少种不同的方法可以爬到楼顶呢？
     * <p>
     * 注意：给定 n 是一个正整数。
     *
     * @param n
     * @return
     */
    public int climbStairs(int n) {
        if (n <= 2) {
            return n;
        }
        int[] dp = new int[n];
        dp[0] = 1;
        dp[1] = 2;
        for (int i = 2; i < n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n - 1];
    }

    public int climbStairsV2(int n) {
        if (n <= 2) {
            return n;
        }
        int pre = 1;
        int second = 2;
        for (int i = 2; i < n; i++) {
            int cur = second + pre;
            pre = second;
            second = cur;
        }
        return second;
    }
}

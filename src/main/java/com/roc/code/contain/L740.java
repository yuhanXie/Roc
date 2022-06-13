package com.roc.code.contain;

/**
 * @author xiegang
 */
public class L740 {

    public static void main(String[] args) {
        L740 l740 = new L740();
        System.out.println(l740.minCostClimbingStairs(new int[]{10, 15, 20}));
        System.out.println(l740.minCostClimbingStairs(new int[]{1, 100, 1, 1, 1, 100, 1, 1, 100, 1}));
        System.out.println(l740.minCostClimbingStairsV2(new int[]{10, 15, 20}));
        System.out.println(l740.minCostClimbingStairsV2(new int[]{1, 100, 1, 1, 1, 100, 1, 1, 100, 1}));
    }

    public int minCostClimbingStairs(int[] cost) {
        //dp[n] = min(dp[n - 1] + cost[n-1], dp[n-2] + cost[n-2])
        int[] dp = new int[cost.length + 1];
        for (int i = 2; i <= cost.length; i++) {
            dp[i] = Math.min(dp[i - 1] + cost[i - 1], dp[i - 2] + cost[i - 2]);
        }
        return dp[cost.length];
    }

    public int minCostClimbingStairsV2(int[] cost) {
        //dp[n] = min(dp[n - 1] + cost[n-1], dp[n-2] + cost[n-2])
        int pre = 0;
        int second = 0;
        for (int i = 2; i <= cost.length; i++) {
            int cur = Math.min(pre + cost[i - 2], second + cost[i-1]);
            pre = second;
            second = cur;
        }
        return second;
    }


}

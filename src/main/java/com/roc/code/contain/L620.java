package com.roc.code.contain;


/**
 * @author xiegang
 */
public class L620 {

    public static void main(String[] args) {
        L620 l620 = new L620();
        System.out.println(l620.kInversePairsV2(3, 0));
        System.out.println(l620.kInversePairsV2(3, 1));
    }

    /**
     * L629. K个逆序对数组
     *
     * 给出两个整数 n 和 k，找出所有包含从 1 到 n 的数字，且恰好拥有 k 个逆序对的不同的数组的个数。
     *
     * 逆序对的定义如下：对于数组的第i个和第 j个元素，如果满i < j且 a[i] > a[j]，则其为一个逆序对；否则不是。
     *
     * 由于答案可能很大，只需要返回 答案 mod 109 + 7 的值。
     *
     * @param n
     * @param k
     * @return
     */
    public int kInversePairs(int n, int k) {
        int mod = (int) (Math.pow(10, 9) + 7);
        //元素从0开始，好理解一些 dp[0][j] = 0;
        //dp[i][j]表示1-i共i个元素j个逆序对的数量
        //dp[1][0] = 1; dp[1][j]=0;
        //dp[i][0] = 1;
        int[][] dp = new int[n + 1][k + 1];
        dp[1][0] = 1;
        for (int i = 2; i <= n; i++) {
            for (int j = 0; j <= k; j++) {
                if (j == 0) {
                    dp[i][j] = 1;
                } else {
                    for (int m = 0; m <= i - 1; m++) {
                        if (j >= i - 1 - m) {
                            dp[i][j] += dp[i - 1][j - (i - 1 - m)];
                            if (dp[i][j] >= mod) {
                                //大于之后减掉
                                dp[i][j] -= mod;
                            } else if (dp[i][j] < 0) {
                                //超出范围之后会变成负数，加上mod
                                dp[i][j] += mod;
                            }
                        }
                    }
                }
            }
        }
        return dp[n][k];
    }


    public int kInversePairsV2(int n, int k) {
        int mod = (int) (Math.pow(10, 9) + 7);
        //元素从0开始，好理解一些 dp[0][j] = 0;
        //dp[i][j]表示1-i共i个元素j个逆序对的数量
        //dp[1][0] = 1; dp[1][j]=0;
        //dp[i][0] = 1;

        //当前状态的计算只依赖上个i-1的数组。那么只要维护一个二维的k+1数组即可
        int[][] dp = new int[2][k + 1];
        dp[0][0] = 1;
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j <= k; j++) {
                for (int m = 0; m <= i - 1; m++) {
                    //奇数和1相与 = 1 偶数和1相与 = 0;
                    int cur = i & 1;
                    int pre = cur ^ 1;
                    if (j >= i - 1 - m) {
                        dp[cur][j] += dp[pre][j - (i - 1 - m)];
                        if (dp[cur][j] >= mod) {
                            //大于之后减掉
                            dp[cur][j] -= mod;
                        } else if (dp[cur][j] < 0) {
                            //超出范围之后会变成负数，加上mod
                            dp[cur][j] += mod;
                        }
                    }
                }
            }
        }
        return dp[n & 1][k];
    }


    //暴力：首先循环出所有排列组合，时间复杂度：n！
    //对每一个组合，进行计算其逆序对的数量,若等于k，则+1，复杂度 n！*n*n

    //尝试用动态规划的思路，
    // dp[i][j]表示前i个元素逆序对数量为j的序列数量。 我们就求dp[n][k]
    //状态转移方程还是比较难思考的
    //原先i-1的所有序列中，现在需要插入i这个元素，假设插入的位置是m，
    // 那么它与前面m个元素都不够成逆序对，和后面i-1-m的元素就构成逆序对，逆序对数量也就是i-1-m，并且它不会影响原先序列的逆序对的数量
    // 当前序列就是i个元素，逆序对数量是（i-1-m + 当前序列的数量）的一个序列
    // 设i-1-m + 当前序列的数量 = j， 所有这样的序列的总和就是dp[i][j]
    // dp[i][j] = m从[0,i-1] 求和 dp[i-1][j - (i-1-m)]
    // n 的范围是 [1, 1000] 并且 k 的范围是 [0, 1000]。
    // dp[i][0] = 1
    // dp[0][j] = 0;

}

package com.roc.code.contain;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author xiegang
 */
public class L1210 {

    public static void main(String[] args) {
        L1210 l1210 = new L1210();
        System.out.println(l1210.longestSubsequenceV2(new int[]{1, 2, 3, 4}, 1));
        System.out.println(l1210.longestSubsequenceV2(new int[]{1, 3, 5, 7}, 1));
        System.out.println(l1210.longestSubsequenceV2(new int[]{1, 5, 7, 8, 5, 3, 4, 2, 1}, -2));
        //3
        System.out.println(l1210.longestSubsequenceV2(new int[]{16, -4, -6, -11, -8, -9, 4, -11, 15, 15, -9, 11,
                7, -7, 10, -16, 4}, 3));


        System.out.println(l1210.longestSubsequenceV2(new int[]{4, 12, 10, 0, -2, 7, -8, 9, -9, -12, -12, 8, 8}, 0));

    }

    public int longestSubsequence(int[] arr, int difference) {
        //暴力 以每个元素开始，向后找差difference的数据，最后比较大小即可 o(n*n)
        //dp[i]表示以当前元素结尾的数组中子序列的长度,index[i]表示元素所在位置
        //dp[i] = dp[index[(arr[i] - difference)]] + 1
        int n = arr.length;
        HashMap<Integer, Integer> indexMap = new HashMap<>();
        //需要考虑如果出现重复元素该怎么处理
//        for (int i = 0; i < n; i++) {
//            indexMap.put(arr[i], i);
//        }
        int[] dp = new int[n];
        Arrays.fill(dp, 1);
        int result = 1;
        for (int i = 0; i < n; i++) {
            int need = arr[i] - difference;
            int needIndex = indexMap.getOrDefault(need, n);
            if (needIndex < i) {
                dp[i] = dp[needIndex] + 1;
                result = Math.max(result, dp[i]);
            }
            indexMap.put(arr[i], i);
        }
        return result;
    }

    /**
     * L1218. 最长定差子序列
     * <p>
     * 给你一个整数数组 arr 和一个整数 difference，请你找出并返回 arr 中最长等差子序列的长度，该子序列中相邻元素之间的差等于 difference 。
     * <p>
     * 子序列 是指在不改变其余元素顺序的情况下，通过删除一些元素或不删除任何元素而从 arr 派生出来的序列。
     * <p>
     * <p>
     * <p>
     * 示例 1：
     * <p>
     * 输入：arr = [1,2,3,4], difference = 1
     * 输出：4
     * 解释：最长的等差子序列是 [1,2,3,4]。
     * <p>
     * 示例 2：
     * <p>
     * 输入：arr = [1,3,5,7], difference = 1
     * 输出：1
     * 解释：最长的等差子序列是任意单个元素。
     * <p>
     * 示例 3：
     * <p>
     * 输入：arr = [1,5,7,8,5,3,4,2,1], difference = -2
     * 输出：4
     * 解释：最长的等差子序列是 [7,5,3,1]。
     * <p>
     * <p>
     * <p>
     * 提示：
     * <p>
     * 1 <= arr.length <= 105
     * -104 <= arr[i], difference <= 104
     *
     * @param arr
     * @param difference
     * @return
     */
    public int longestSubsequenceV2(int[] arr, int difference) {
        //dp[i]表示以当前元素结尾的数组中子序列的长度
        //前面处理复杂了，核心是为了找到dp[arr[i] - difference],而我先记了num->index->再到num，多次一举了
        //直接使用map，key：arr[i]，value：最小子序列的长度即可
        int result = 1;
        HashMap<Integer, Integer> dpMap = new HashMap<>();
        for (int j : arr) {
            int count = dpMap.getOrDefault(j - difference, 0) + 1;
            dpMap.put(j, count);
            result = Math.max(result, count);
        }
        return result;
    }


}

package com.roc.code.contain;

import java.util.HashMap;

/**
 * @author xiegang
 */
public class L590 {

    public static void main(String[] args) {
        L590 l590 = new L590();
        System.out.println(l590.maxCount(3, 3, new int[][]{{2, 2}, {3, 3}}));
    }

    public int maxCount(int m, int n, int[][] ops) {
        //找ops里面最小的交集， 可能存在没有交集的情况
        //暴力法,超出内存限制
        int[][] source = new int[m][n];

        for (int[] op : ops) {
            for (int k = 0; k < op[0]; k++) {
                for (int l = 0; l < op[1]; l++) {
                    source[k][l]++;
                }
            }
        }
        int max = 0;
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                max = Math.max(max, source[i][j]);
                map.put(source[i][j], map.getOrDefault(source[i][j], 0) + 1);
            }
        }

        return map.get(max);
    }

    /**
     * L598. 范围求和 II
     *
     * @param m
     * @param n
     * @param ops
     * @return
     */
    public int maxCountV2(int m, int n, int[][] ops) {
        //找ops里面最小的交集， 不存在没有交集的情况，都是从[0,0]开始，只要找最小值即可
        int minX = m;
        int minY = n;
        for (int[] op : ops) {
            minX = Math.min(minX, op[0]);
            minY = Math.min(minY, op[1]);
        }
        return minX * minY;
    }


}

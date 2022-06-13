package com.roc.code.contain.l400;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author gang.xie
 */
public class L410 {

    public static void main(String[] args) {
        L410 l410 = new L410();

        List<List<Integer>> list = l410.pacificAtlantic(new int[][]{{1,2,2,3,5},{3,2,3,4,4},{2,4,5,3,1},{6,7,1,4,5},{5,1,1,2,4}});
//        List<List<Integer>> list = l410.pacificAtlantic(new int[][]{{2,1},{1,2}});
        for (List<Integer> subList : list) {
            subList.forEach(System.out::print);
            System.out.println();
        }
    }


    /**
     * L417
     * <p>
     * m == heights.length
     * n == heights[r].length
     * 1 <= m, n <= 200
     * 0 <= heights[r][c] <= 105
     *
     * @param heights
     * @return
     */
    public List<List<Integer>> pacificAtlantic(int[][] heights) {
        //从与海边相邻的格子开始递推，如果下一个高于当前，说明可抵达海边
        //分为两个海，最后求并集
        int m = heights.length;
        int n = heights[0].length;
        boolean[][] pacific = new boolean[m][n];
        boolean[][] atlantic = new boolean[m][n];
        for (int j = 0; j < n; j++) {
            dfs(pacific, heights, 0, j);
            dfs(atlantic, heights, m - 1, j);
        }

        for (int i = 0; i < m; i++) {
            dfs(pacific, heights, i, 0);
            dfs(atlantic, heights, i, n - 1);
        }


        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (pacific[i][j] && atlantic[i][j]) {
                    result.add(Arrays.asList(i, j));
                }
            }
        }
        return result;
    }


    int[][] vector = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    private void dfs(boolean[][] ocean, int[][] heights, int i, int j) {
        if (ocean[i][j]) {
            return;
        }
        ocean[i][j] = true;

        for (int k = 0; k < 4; k++) {
            int[] around = vector[k];
            int newI = i + around[0];
            int newJ = j + around[1];
            if (newI >= 0 && newI < heights.length
                    && newJ >= 0 && newJ < heights[0].length
                    && heights[newI][newJ] >= heights[i][j]) {
                dfs(ocean, heights, newI, newJ);
            }
        }
    }
}

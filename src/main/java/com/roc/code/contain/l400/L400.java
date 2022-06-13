package com.roc.code.contain.l400;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @author xiegang
 */
public class L400 {

    public static void main(String[] args) {
        L400 l400 = new L400();
        int[][] heightMap = new int[][]{{1, 4, 3, 1, 3, 2}, {3, 2, 1, 3, 2, 4}, {2, 3, 3, 2, 3, 1}};
        int[][] heightMap2 = new int[][]{{3, 3, 3, 3, 3}, {3, 2, 2, 2, 3}, {3, 2, 1, 2, 3}, {3, 2, 2, 2, 3}, {3, 3, 3, 3, 3}};
        int[][] heightMap3 = new int[][]{{12, 13, 1, 12}, {13, 4, 13, 12}, {13, 8, 10, 12}, {12, 13, 12, 12}, {13, 13, 13, 13}};
        System.out.println(l400.trapRainWaterV2(heightMap));
        System.out.println(l400.trapRainWaterV2(heightMap2));
        System.out.println(l400.trapRainWaterV2(heightMap3));
    }

    /**
     * 407. 接雨水 II
     * <p>
     * 给你一个 m x n 的矩阵，其中的值均为非负整数，代表二维高度图每个单元的高度，请计算图中形状最多能接多少体积的雨水。
     *
     * @param heightMap
     * @return
     */
    public int trapRainWater(int[][] heightMap) {
        //错误方式
        //同1类似，也求每个列可以盛水的数量。
        // 要求前后左右四个维度的最低值，如果大于当前高度，即min - height[i][j]即可
        int m = heightMap.length;
        int n = heightMap[0].length;
        int[][] left = new int[m][n];
        int[][] right = new int[m][n];
        int[][] up = new int[m][n];
        int[][] down = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 1; j < n; j++) {
                left[i][j] = Math.max(left[i][j - 1], heightMap[i][j - 1]);
            }
            for (int j = n - 2; j >= 0; j--) {
                right[i][j] = Math.max(right[i][j + 1], heightMap[i][j + 1]);
            }
        }

        for (int j = 0; j < n; j++) {
            for (int i = 1; i < m; i++) {
                up[i][j] = Math.max(up[i - 1][j], heightMap[i - 1][j]);
            }
            for (int i = m - 2; i >= 0; i--) {
                down[i][j] = Math.max(down[i + 1][j], heightMap[i + 1][j]);
            }
        }

        int sum = 0;
        for (int i = 1; i < m - 1; i++) {
            for (int j = 1; j < n - 1; j++) {
                int min = Math.min(Math.min(left[i][j], right[i][j]), Math.min(up[i][j], down[i][j]));
                sum += min > heightMap[i][j] ? min - heightMap[i][j] : 0;
            }
        }
        return sum;
    }

    // 开始的思路很简单，就是从一维推到二维，解决方式是一样的，但是提交代码之后啪啪打脸，并不能通过。
    //然后去看了题解，但是并没有能理解为什么一维推到二维的方式 是行不通的，题解里也没有给出，最后是在一个题解中分析出来的
    //以图中标蓝的2为例，它上下左右的最大值分别为：7，8，12，12，以一维的思路，他能接的雨水的最大值就是7 - 2 = 5；
    //那我们再往上看5，旁边4是小于5的，那么5肯定是不能接雨水的。那么问题就来了，2接了雨水高度到了7
    //但是5不能接雨水，7的雨水就没办法存储，会留到5，接着就沿着左侧的4流出去了。
    //二维的复杂度在于它不仅受到上下左右高度的影响，也受到斜角的影响
    // 再转头过来看题解
    public int trapRainWaterV2(int[][] heightMap) {
        int m = heightMap.length;
        int n = heightMap[0].length;
        boolean[][] visited = new boolean[m][n];
        //数组记录位置i，j和height
        PriorityQueue<int[]> queue = new PriorityQueue<>(Comparator.comparingInt(o -> o[2]));
        //将数组四周的元素先添加到优先级队列
        for (int i = 0; i < m; i++) {
            queue.offer(new int[]{i, 0, heightMap[i][0]});
            queue.offer(new int[]{i, n - 1, heightMap[i][n - 1]});
            visited[i][0] = true;
            visited[i][n - 1] = true;
        }
        for (int j = 1; j < n - 1; j++) {
            queue.offer(new int[]{0, j, heightMap[0][j]});
            queue.offer(new int[]{m - 1, j, heightMap[m - 1][j]});
            visited[0][j] = true;
            visited[m - 1][j] = true;
        }

        int result = 0;
        int[][] dir = new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        while (!queue.isEmpty()) {
            //每次取最小元素,表示当前围墙的最低高度
            int[] cur = queue.poll();
            for (int[] ints : dir) {
                int newX = cur[0] + ints[0];
                int newY = cur[1] + ints[1];
                //向四周扩散
                if (newX >= 0 && newX < m && newY >= 0 && newY < n && !visited[newX][newY]) {
                    //新元素高度比当前围墙最低高度还要低，那么这快是可以接住雨水的
                    if (cur[2] > heightMap[newX][newY]) {
                        result += cur[2] - heightMap[newX][newY];
                    }
                    //围墙往里缩
                    queue.offer(new int[]{newX, newY, Math.max(cur[2], heightMap[newX][newY])});
                    visited[newX][newY] = true;
                }
            }
        }
        return result;
    }
}

package com.roc.code.contain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author gang.xie
 */
public class L670 {

    public static void main(String[] args) {
        L670 l670 = new L670();
        List<List<Integer>> forest = new ArrayList<>();
//        forest.add(Arrays.asList(2, 3, 4));
//        forest.add(Arrays.asList(0, 0, 0));
//        forest.add(Arrays.asList(8, 7, 6));
        forest.add(Arrays.asList(4,2,3));
        forest.add(Arrays.asList(0,0,1));
        forest.add(Arrays.asList(7,6,5));

        System.out.println(l670.cutOffTree(forest));
    }

    /**
     * L675. 为高尔夫比赛砍树
     * m == forest.length
     * n == forest[i].length
     * 1 <= m, n <= 50
     * 0 <= forest[i][j] <= 109
     * 你需要按照树的高度从低向高砍掉所有的树，每砍过一颗树，该单元格的值变为 1（即变为地面）。
     * 没有两棵树的高度是相同的
     *
     * @param forest
     * @return
     */
    public int cutOffTree(List<List<Integer>> forest) {
        m = forest.size();
        n = forest.get(0).size();
        this.forest = forest;
        //找到所有大于0的点
        List<int[]> list = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (forest.get(i).get(j) > 1) {
                    list.add(new int[]{forest.get(i).get(j), i, j});
                }
            }
        }
        //排序
        list.sort(Comparator.comparingInt(a -> a[0]));
        if (forest.get(0).get(0) == 0) {
            return -1;
        }
        int x = 0;
        int y = 0;
        int step = 0;
        for (int[] ints : list) {
            int newX = ints[1];
            int newY = ints[2];
            int curStep = search(x, y, newX, newY);
            if (curStep == -1) {
                return -1;
            } else {
                step += curStep;
            }
            x = newX;
            y = newY;
        }

        return step;
    }
    int[][] vector = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    int m;
    int n;
    List<List<Integer>> forest;

    private int search(int x, int y, int tx, int ty) {
        if (x == tx && y == ty) {
            return 0;
        }
        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[m][n];
        queue.offer(new int[]{x, y});
        visited[x][y] = true;
        int step = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int k = 0; k < size; k++) {
                int[] cur = queue.poll();
                for (int i = 0; i < 4; i++) {
                    int newX = cur[0] + vector[i][0];
                    int newY = cur[1] + vector[i][1];
                    if (newX >= 0 && newX < m
                            && newY >= 0 && newY < n
                            && !visited[newX][newY]) {
                        if (newX == tx && newY == ty) {
                            return step + 1;
                        }
                        if (forest.get(newX).get(newY) > 0) {
                            queue.offer(new int[]{newX, newY});
                        }
                        visited[newX][newY] = true;
                    }
                }
            }
            step++;
        }
        return -1;
    }
}

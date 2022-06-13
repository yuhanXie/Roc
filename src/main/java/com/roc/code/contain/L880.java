package com.roc.code.contain;

/**
 * @author gang.xie
 */
public class L880 {


    public static void main(String[] args) {
        L880 l880 = new L880();
        System.out.println(l880.projectionArea(new int[][]{{1, 2}, {3, 4}}));
        System.out.println(l880.projectionArea(new int[][]{{2}}));
        System.out.println(l880.projectionArea(new int[][]{{1,0},{0,2}}));
    }

    /**
     * L883
     *
     *
     * @param grid grid
     * @return int
     */
    public int projectionArea(int[][] grid) {
        //xy投影面：往下看的总面积就是 数组中不为0的数量总和
        //xz投影面：同一行最大值之和
        //yz投影面：同一列最大值之和 [[1,2],[3,4]]
        int xy = 0;
        int xz = 0;
        int yz = 0;
        int col = grid[0].length;
        for (int[] intx : grid) {
            int max = 0;
            for (int temp : intx) {
                if (temp != 0) {
                    xy++;
                }
                max = Math.max(max, temp);
            }
            xz += max;
        }

        for (int j = 0; j < col; j++) {
            int max = 0;
            for (int[] ints : grid) {
                max = Math.max(max, ints[j]);
            }
            yz += max;
        }

        return xy + xz + yz;
    }

    /**
     * n == grid.length == grid[i].length
     *
     * @param grid
     * @return
     */
    public int projectionAreaV2(int[][] grid) {
        int xy = 0;
        int xz = 0;
        int yz = 0;
        int row = grid.length;
        for (int i = 0; i < row; i++) {
            int maxXZ = 0;
            int maxYZ = 0;
            for (int j = 0; j < row; j++) {
                if (grid[i][j] != 0) {
                    xy++;
                }
                maxXZ = Math.max(maxXZ, grid[i][j]);
                //grid长宽是一致的，否则无法直接求
                maxYZ = Math.max(maxYZ, grid[j][i]);
            }
            xz += maxXZ;
            yz += maxYZ;
        }
        return xy + xz + yz;
    }
}

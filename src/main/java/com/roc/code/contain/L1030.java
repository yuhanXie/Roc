package com.roc.code.contain;

/**
 * @author gang.xie
 */
public class L1030 {

    public static void main(String[] args) {
        L1030 l1030 = new L1030();
//        System.out.println(l1030.isBoomerang(new int[][]{{1, 1}, {2, 3}, {3, 2}}));
//        System.out.println(l1030.isBoomerang(new int[][]{{1, 1}, {2, 2}, {3, 3}}));
        System.out.println(l1030.isBoomerang(new int[][]{{0, 0}, {0, 2}, {2, 1}}));
    }

    /**
     * L1037. 有效的回旋镖
     *
     * @param points
     * @return
     */
    public boolean isBoomerang(int[][] points) {
        //判断斜率是否一样即可
        int k1 = (points[2][1] - points[1][1]) * (points[2][0] - points[0][0]);
        int k2 = (points[2][1] - points[0][1]) * (points[2][0] - points[1][0]);
        return k1 != k2;
    }
}

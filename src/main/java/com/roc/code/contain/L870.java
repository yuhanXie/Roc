package com.roc.code.contain;

import java.util.Arrays;

/**
 * @author gang.xie
 */
public class L870 {


    public static void main(String[] args) {
        L870 l870 = new L870();
        System.out.println(l870.minEatingSpeed(new int[]{3, 6, 7, 11}, 8));
        System.out.println(l870.minEatingSpeed(new int[]{30,11,23,4,20}, 5));
        System.out.println(l870.minEatingSpeed(new int[]{30,11,23,4,20}, 6));
        System.out.println(l870.minEatingSpeed(new int[]{312884470}, 968709470));
    }


    /**
     * L875. 爱吃香蕉的珂珂
     *
     * @param piles
     * @param h
     * @return
     */
    public int minEatingSpeed(int[] piles, int h) {
        //二分法查询
        //最小值0，最大值就是piles里的最大值
        //求这个范围内的符合小于h 的最大值
        //其实就是求一个边界值，这个值的时间正好小于等于h，这个值+1就大于h了
        int right = Arrays.stream(piles).max().orElse(0);
        int left = 1;
        int result = right;
        while (left < right) {
            int mid = (left + right) >> 1;
            int time = getTime(piles, mid);
            if (time <= h) {
                result = mid;
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return result;
    }

    private int getTime(int[] piles, int speed) {
        int time = 0;
        for (int pile : piles) {
            time += pile / speed + (pile % speed == 0 ? 0 : 1);
        }
        return time;
    }


}

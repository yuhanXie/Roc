package com.roc.code.contain.l400;

import java.util.Arrays;

/**
 * @author gang.xie
 */
public class L470 {

    public static void main(String[] args) {
        L470 l470 = new L470();
        System.out.println(l470.makesquare(new int[]{1, 1, 2, 2, 2}));
        System.out.println(l470.makesquare(new int[]{1, 2, 3, 4, 5, 1}));
    }


    /**
     * L473 火柴拼正方形
     *
     * @param matchsticks
     * @return
     */
    public boolean makesquare(int[] matchsticks) {
        //总和应该是4的倍数
        //若干个加起来 可以得到边长的长度
        int sum = Arrays.stream(matchsticks).sum();
        if (sum % 4 != 0) {
            return false;
        }
        //从大到小排序
        bubbleSortV2(matchsticks);
        System.out.println(Arrays.toString(matchsticks));

        // 尝试使用回溯法，只要拼出4个边长，且没有剩余即可
        int edge = sum / 4;
        return backtrack(matchsticks, edge, 0, new int[4]);
    }

    private boolean backtrack(int[] matchsticks, int edge, int index, int[] edges) {
        if (index == matchsticks.length) {
            return Arrays.stream(edges).allMatch(val -> val == edge);
        }
        for (int i = 0; i < 4; i++) {
            int cur = edges[i];
            if (cur + matchsticks[index] > edge) {
                continue;
            }
            edges[i] += matchsticks[index];
            if (backtrack(matchsticks, edge, index + 1, edges)) {
                return true;
            }
            edges[i] -= matchsticks[index];
        }

        return false;

    }

    private void bubbleSortV2(int[] array) {
        int sortBorder = 0;
        for (int i = 0; i < array.length; i++) {
            boolean isSorted = true;
            int sortedBorder = sortBorder;
            for (int j = array.length - 1; j > sortedBorder; j--) {
                if (array[j] > array[j - 1]) {
                    int temp = array[j - 1];
                    array[j - 1] = array[j];
                    array[j] = temp;
                    sortBorder = j;
                    isSorted = false;
                }
            }
            if (isSorted) {
                break;
            }
        }
    }

    /**
     * 将最大元素放到队首
     *
     * @param array
     */
    private void bubbleSort(int[] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = array.length - 1; j > 0; j--) {
                if (array[j] > array[j - 1]) {
                    int temp = array[j - 1];
                    array[j - 1] = array[j];
                    array[j] = temp;
                }
            }
        }
    }


}

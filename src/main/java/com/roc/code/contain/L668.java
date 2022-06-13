package com.roc.code.contain;

/**
 * @author gang.xie
 */
public class L668 {

    public static void main(String[] args) {
        L668 l668 = new L668();
//        System.out.println(l668.findKthNumber(3, 3, 5));
//        System.out.println(l668.findKthNumber(2, 3, 6));
        System.out.println(l668.findKthNumber(2, 3, 6));
    }

    /**
     * L668
     *
     * @param m
     * @param n
     * @param k
     * @return
     */
    public int findKthNumber(int m, int n, int k) {
        //二分法
        if (m < n) {
            int temp = n;
            n = m;
            m = temp;
        }
        int left = 1;
        int right = m * n;
        while (left < right) {
            int mid = (left + right) >> 1;
            int count = getCount(m, n, mid);
            if (count >= k) {
                right = mid;
            } else {
                left = mid + 1;
            }

        }

        return right;

    }

    private int getCount(int m, int n, int mid) {
        int a = 0;
        for (int i = 1; i <= n; i++) {
            if (i * m < mid) {
                a += m;
            } else {
                a += mid / i;
            }
        }
        return a;
    }


    public int findKthNumberV2(int m, int n, int k) {
        int left = 1;
        int right = m * n;
        while (left < right) {
            int mid = (left + right) >> 1;
            int count = mid / n * n;
            for (int i = mid / n + 1; i <= m; ++i) {
                count += mid / i;
            }
            if (count >= k) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return right;
    }



}

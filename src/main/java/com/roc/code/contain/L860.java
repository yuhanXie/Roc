package com.roc.code.contain;

/**
 * @author gang.xie
 */
public class L860 {

    public static void main(String[] args) {
        L860 l860 = new L860();
        System.out.println(l860.binaryGap(22));
        System.out.println(l860.binaryGap(8));
        System.out.println(l860.binaryGap(5));
        System.out.println(l860.binaryGapV2(22));
        System.out.println(l860.binaryGapV2(8));
        System.out.println(l860.binaryGapV2(5));
    }

    /**
     * L868 二进制间距
     *
     * @param n
     * @return
     */
    public int binaryGap(int n) {
        int result = 0;
        int last = -1;
        while (n != 0) {
            //取最低位的1， -n是n的原码取反之后+1
            int temp = n & -n;
            int cur = (int) (Math.log(temp) / Math.log(2));
            if (last >= 0) {
                result = Math.max(result, cur - last);
            }
            last = cur;
            //将最低位的1置为0。
            n -= temp;
        }
        return result;
    }

    public int binaryGapV2(int n) {
        int last = -1;
        int result = 0;
        for (int i = 0; i <= 31; i++) {
            if ((n & 1) == 1) {
                if (last >= 0) {
                    result = Math.max(result, i - last);
                }
                last = i;
            }
            n = n >> 1;
        }
        return result;
    }
}

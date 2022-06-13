package com.roc.code.contain;

/**
 * @author gang.xie
 */
public class L0105 {

    public static void main(String[] args) {
        L0105 l0105 = new L0105();
        System.out.println(l0105.oneEditAway("palee", "pale"));
        System.out.println(l0105.oneEditAway("peale", "pale"));
        System.out.println(l0105.oneEditAway("pele", "pale"));
    }

    /**
     * 面试题 01.05. 一次编辑
     * 字符串有三种编辑操作:插入一个字符、删除一个字符或者替换一个字符。
     * 给定两个字符串，编写一个函数判定它们是否只需要一次(或者零次)编辑。
     *
     * @param first
     * @param second
     * @return
     */
    public boolean oneEditAway(String first, String second) {
        int m = first.length();
        int n = second.length();
        if (Math.abs(m - n) >= 2) {
            return false;
        }

        //palee  peale
        //pale   pale
        if (Math.abs(m - n) == 1) {
            String longer = m > n ? first : second;
            String shorter = m > n ? second : first;
            //first需要删除一个元素
            int shortIndex = 0;
            int longIndex = 0;
            boolean isDeleted = false;
            while (shortIndex < shorter.length()) {
                if (shorter.charAt(shortIndex) == longer.charAt(longIndex)) {
                    shortIndex++;
                } else {
                    if (isDeleted) {
                        return false;
                    }
                    isDeleted = true;
                }
                longIndex++;
            }
            return true;
        }

        boolean isReplace = false;
        for (int i = 0; i < m; i++) {
            if (first.charAt(i) != second.charAt(i)) {
                if (isReplace) {
                    return false;
                }
                isReplace = true;
            }
        }
        return true;
    }

    public boolean oneEditAwayV2(String first, String second) {
        int m = first.length();
        int n = second.length();
        if (Math.abs(m - n) >= 2) {
            return false;
        }
        String longer = m >= n ? first : second;
        String shorter = m >= n ? second : first;
        int longIndex = 0;
        int shortIndex = 0;
        boolean isDiff = false;
        while (shortIndex < shorter.length() && longIndex < longer.length()) {
            if (shorter.charAt(shortIndex) == longer.charAt(longIndex)) {
                shortIndex++;
                longIndex++;
            } else {
                if (isDiff) {
                    return false;
                }
                isDiff = true;
                if (longer.length() == shorter.length()) {
                    shortIndex++;
                }
                longIndex++;
            }
        }
        return true;

    }
}

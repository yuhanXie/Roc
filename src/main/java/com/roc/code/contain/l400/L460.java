package com.roc.code.contain.l400;

import java.util.Arrays;

/**
 * @author gang.xie
 */
public class L460 {

    public static void main(String[] args) {
        L460 l460 = new L460();
//        System.out.println(l460.minMoves2(new int[]{1, 10, 2, 9}));
//        System.out.println(l460.findSubstringInWraproundString("a"));
//        System.out.println(l460.findSubstringInWraproundString("cac"));
//        System.out.println(l460.findSubstringInWraproundString("zab"));
//        System.out.println(l460.validIPAddress("172.16.254.1"));
//        System.out.println(l460.validIPAddress("2001:0db8:85a3:0:0:8A2E:0370:7334"));
//        System.out.println(l460.validIPAddress("256.256.256.256"));
//        System.out.println(l460.validIPAddress("2001:0db8:85a3:0:0:8A2E:0370:733l"));
        System.out.println(l460.validIPAddress("2001:0db8:85a3:0:0:8A2E:0370:7334:"));
        System.out.println(l460.validIPAddress("12..33.4"));
        System.out.println(l460.validIPAddress("1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111.1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111.1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111.1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111"));
    }


    /**
     * L462 最少移动次数使数组元素相等 II
     * 给你一个长度为 n 的整数数组 nums ，返回使所有数组元素相等需要的最少移动数。
     * <p>
     * 在一步操作中，你可以使数组中的一个元素加 1 或者减 1 。
     *
     * @param nums
     * @return
     */
    public int minMoves2(int[] nums) {
        //经数学校验，将数组元素都变为中位数的时候，移动数量最少
        Arrays.sort(nums);
        int midVal = nums[nums.length / 2];
        int result = 0;
        for (int num : nums) {
            result += Math.abs(midVal - num);
        }
        return result;
    }

    /**
     * L467
     *
     * @param p
     * @return
     */
    public int findSubstringInWraproundString(String p) {
        // a b c d  f  6
        // a b c d  e g   10
        // z a x z
        //动态规划，dp[i]表示字母i 在p中以字母i结尾的最长子串的长度
        int[] dp = new int[26];
        int length = 0;
        for (int i = 0; i < p.length(); i++) {
            if (i > 0 && (p.charAt(i) - p.charAt(i - 1) == 1
                    || p.charAt(i) - p.charAt(i - 1) == -25)) {
                length++;
            } else {
                length = 1;
            }
            dp[p.charAt(i) - 'a'] = Math.max(dp[p.charAt(i) - 'a'], length);
        }
        return Arrays.stream(dp).sum();
    }


    public String validIPAddress(String queryIP) {
        if (queryIP.contains(".")) {
            if (queryIP.startsWith(".") || queryIP.endsWith(".")) {
                return "Neither";
            }
            //check ipv4
            String[] array = queryIP.split("\\.");
            if (array.length != 4) {
                return "Neither";
            }
            for (String ip : array) {
                if (ip.isEmpty() || ip.length() > 3) {
                    return "Neither";
                }
                //0-255,不能包含前导0
                if (ip.length() >= 2 && ip.startsWith("0")) {
                    return "Neither";
                }
                for (char ch : ip.toCharArray()) {
                    if (!Character.isDigit(ch)) {
                        return "Neither";
                    }
                }
                int temp = Integer.parseInt(ip);
                if (temp < 0 || temp > 255) {
                    return "Neither";
                }
            }
            return "IPv4";
        }

        if (queryIP.contains(":")) {
            if (queryIP.startsWith(":") || queryIP.endsWith(":")) {
                return "Neither";
            }
            String[] array = queryIP.split(":");
            if (array.length != 8) {
                return "Neither";
            }
            for (String ip : array) {
                // 1 <= xi.length <= 4
                //可以包含数字、小写英文字母( 'a' 到 'f' )和大写英文字母( 'A' 到 'F' )
                //允许前导0
                if (ip.length() < 1 || ip.length() > 4) {
                    return "Neither";
                }
                for (char ch : ip.toCharArray()) {
                    if (!((ch >= '0' && ch <= '9')
                            || (ch >= 'a' && ch <= 'f')
                            || (ch >= 'A' && ch <= 'F'))) {
                        return "Neither";
                    }
                }
            }
            return "IPv6";
        }
        return "Neither";
    }
}

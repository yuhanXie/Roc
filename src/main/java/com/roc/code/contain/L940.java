package com.roc.code.contain;

/**
 * @author gang.xie
 */
public class L940 {

    public static void main(String[] args) {
        L940 l940 = new L940();
//        int[] result = l940.diStringMatch("IDID");
//        int[] result = l940.diStringMatch("DDI");
//        Arrays.stream(result).forEach(System.out::println);
        System.out.println(l940.minDeletionSize(new String[]{"cba","daf","ghi"}));
    }

    /**
     * L942
     *
     * @param s
     * @return
     */
    public int[] diStringMatch(String s) {
        //贪心算法，如果是i，就放最小值，d，放最大值
        int[] result = new int[s.length() + 1];
        int low = 0;
        int high = s.length();
        for (int i = 0; i < s.length(); i++) {
            result[i] = s.charAt(i) == 'D' ? high-- : low++;
        }
        result[s.length()] = high;
        return result;
    }

    /**
     * L944 删列造序
     *
     * @param strs
     * @return
     */
    public int minDeletionSize(String[] strs) {
       int n = strs.length;
       if (n <= 1) {
           return 0;
       }
       int result = 0;
       int m = strs[0].length();
       for (int i = 0; i < m; i++) {
           for (int j = 0; j < n - 1; j++) {
               char cur = strs[j].charAt(i);
               char next = strs[j + 1].charAt(i);
               if (cur > next) {
                   result++;
                   break;
               }
           }
       }
       return result;

    }

}

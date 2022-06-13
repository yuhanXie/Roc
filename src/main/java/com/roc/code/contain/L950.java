package com.roc.code.contain;

/**
 * @author gang.xie
 */
public class L950 {

    public static void main(String[] args) {
        L950 l950 = new L950();
        System.out.println(l950.isAlienSorted(new String[]{"hello", "leetcode"}, "hlabcdefgijkmnopqrstuvwxyz"));
    }

    /**
     * L953. 验证外星语词典
     * 1 <= words.length <= 100
     * 1 <= words[i].length <= 20
     * order.length == 26
     * 在 words[i] 和 order 中的所有字符都是英文小写字母
     *
     * @param words
     * @param order
     * @return
     */
    public boolean isAlienSorted(String[] words, String order) {
        int[] orderArray = new int[26];
        for (int i = 0; i < 26; i ++) {
            orderArray[order.charAt(i) - 'a'] = i;
        }
        for (int i = 0; i < words.length - 1; i++) {
            String cur = words[i];
            String next = words[i + 1];
            for (int j = 0; j < cur.length(); j++) {
                if (j >= next.length()) {
                    return false;
                }
                int curIndex = orderArray[cur.charAt(j) - 'a'];
                int nextIndex = orderArray[next.charAt(j) - 'a'];
                if (curIndex > nextIndex) {
                    return false;
                } else if (curIndex < nextIndex) {
                    break;
                }
            }
        }
        return true;
    }
}

package com.roc.code.contain;

/**
 * @author gang.xie
 */
public class L1711 {


    /**
     * L面试题 17.11. 单词距离
     *
     * @param words
     * @param word1
     * @param word2
     * @return
     */
    public int findClosest(String[] words, String word1, String word2) {
        //暴力法 记录word的index，然后求最小值
        int result = Integer.MAX_VALUE;
        int last1Index = -1;
        int last2Index = -1;
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals(word1)) {
                last1Index = i;
                if (last2Index >= 0) {
                    result = Math.min(result, last1Index - last2Index);
                    if (result == 1) {
                        return result;
                    }
                }
            } else if (words[i].equals(word2)) {
                last2Index = i;
                if (last1Index >= 0) {
                    result = Math.min(result, last2Index - last1Index);
                    if (result == 1) {
                        return result;
                    }
                }
            }
        }
        return result;
    }


}

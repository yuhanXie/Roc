package com.roc.code.contain;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gang.xie
 */
public class L810 {

    public static void main(String[] args) {
        L810 l810 = new L810();
        String result = l810.mostCommonWord("\"Bob hit a ball, the hit BALL flew far after it was hit.\"",
                new String[]{"hit"});
        System.out.println(result);
    }

    /**
     * L819
     *
     * @param paragraph
     * @param banned
     * @return
     */
    public String mostCommonWord(String paragraph, String[] banned) {
        Map<String, Integer> map = new HashMap<>();
        int index = 0;
        while (index < paragraph.length()) {
            StringBuilder sb = new StringBuilder();
            while (index < paragraph.length()) {
                char ch = paragraph.charAt(index);
                if (ch >= 'A' && ch <= 'z') {
                    sb.append(ch);
                    index++;
                } else {
                    index++;
                    break;
                }
            }
            String word = sb.toString().toLowerCase();
            if (!"".equals(word)) {
                int count = map.getOrDefault(word, 0);
                count++;
                map.put(word, count);
            }

        }
        for (String ban : banned) {
            map.put(ban, 0);
        }
        int max = 0;
        String result = "";
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                result = entry.getKey();
            }
        }
        return result;
    }
}

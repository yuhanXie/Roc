package com.roc.code;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author gang.xie
 */
public class L820 {

    public static void main(String[] args) {
        L820 l820 = new L820();
//        int[] result = l820.shortestToChar("loveleetcode", 'e');
//        int[] result = l820.shortestToChar("aaab", 'b');
//        Arrays.stream(result).forEach(System.out::println);

//        int[] result = l820.shortestToCharV2("loveleetcode", 'e');
//        int[] result = l820.shortestToCharV2("aaab", 'b');
//        Arrays.stream(result).forEach(System.out::println);

        String result = l820.toGoatLatin("I speak Goat Latin");
        System.out.println(result);
    }

    /**
     * 821。字符最短距离
     * loveleetcode e：
     *
     * @param s
     * @param c
     * @return
     */
    public int[] shortestToChar(String s, char c) {
        char[] charArray = s.toCharArray();
        Queue<Integer> list = new LinkedList<>();
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] == c) {
                list.add(i);
            }
        }
        int[] result = new int[charArray.length];
        int first = -charArray.length - 1;
        int second = list.poll();
        for (int i = 0; i < charArray.length; i++) {
            if (i < second) {
                result[i] = Math.min(second - i, i - first);
            }
            if (i == second) {
                result[i] = 0;
                first = second;
                if (!list.isEmpty()) {
                    second = list.poll();
                } else {
                    second = 2 * charArray.length + 1;
                }
            }
        }
        return result;
    }

    public int[] shortestToCharV2(String s, char c) {
        int n = s.length();
        int left = -n;
        int[] result = new int[n];
        //找当前字符与其左侧字符c的最小距离
        for (int i = 0; i < n; i++) {
            if (c == s.charAt(i)) {
                left = i;
            }
            result[i] = i - left;
        }

        //找当前字符与其右侧字符c的最小距离
        int right = 2 * n;
        for (int i = n - 1; i >= 0; i--) {
            if (c == s.charAt(i)) {
                right = i;
            }
            result[i] = Math.min(result[i], right - i);
        }
        return result;
    }


    private static final HashSet<Character> set = new HashSet<>();

    static {
        set.add('a');
        set.add('A');
        set.add('e');
        set.add('E');
        set.add('i');
        set.add('I');
        set.add('o');
        set.add('O');
        set.add('u');
        set.add('U');
    }


    /**
     * L824 山羊拉丁文
     *
     * 如果单词以元音开头（'a', 'e', 'i', 'o', 'u'），在单词后添加"ma"。
     * 例如，单词 "apple" 变为 "applema" 。
     * 如果单词以辅音字母开头（即，非元音字母），移除第一个字符并将它放到末尾，之后再添加"ma"。
     * 例如，单词 "goat" 变为 "oatgma" 。
     * 根据单词在句子中的索引，在单词最后添加与索引相同数量的字母'a'，索引从 1 开始。
     * 例如，在第一个单词后添加 "a" ，在第二个单词后添加 "aa" ，以此类推。
     *
     * @param sentence
     * @return
     */
    public String toGoatLatin(String sentence) {
        String[] array = sentence.split(" ");
        StringBuilder append = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            String temp = array[i];
            append.append("a");
            StringBuilder sb = new StringBuilder();
            if (set.contains(temp.charAt(0))) {
                sb.append(temp);
            } else {
                sb.append(temp.substring(1)).append(temp.charAt(0));
            }
            sb.append("ma").append(append);
            array[i] = sb.toString();
        }

        return String.join(" ", array);
    }
}

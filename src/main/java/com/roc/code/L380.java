package com.roc.code;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gang.xie
 */
public class L380 {

    public static void main(String[] args) {
        L380 l380 = new L380();
//        String input = "dir\\n\\tsubdir1\\n\\tsubdir2\\n\\t\\tfile.ext";
//        l380.lengthLongestPath(input);

//        List<Integer> result = l380.lexicalOrder(13);
//        List<Integer> result = l380.lexicalOrderV2(13);
//        result.forEach(System.out::println);
        long test = 180262421507L;
        Date date = new Date(test);
        String result = DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
        System.out.println(result);

    }

    public int lengthLongestPathDemo(String input) {
        int n = input.length();
        int pos = 0;
        int ans = 0;
        int[] level = new int[n + 1];

        while (pos < n) {
            int depth = 1;
            while (pos < n && input.charAt(pos) == '\t') {
                pos++;
                depth++;
            }
            /* 统计当前文件名的长度 */
            int len = 0;
            boolean isFile = false;
            while (pos < n && input.charAt(pos) != '\n') {
                if (input.charAt(pos) == '.') {
                    isFile = true;
                }
                len++;
                pos++;
            }
            /* 跳过换行符 */
            pos++;

            if (depth > 1) {
                len += level[depth - 1] + 1;
            }
            if (isFile) {
                ans = Math.max(ans, len);
            } else {
                level[depth] = len;
            }
        }
        return ans;
    }


    /**
     * L388
     *
     * @param input
     * @return
     */
    public int lengthLongestPath(String input) {
        // dir\n\tsubdir1\n\tsubdir2\n\t\tfile.ext
        // dir\n\tsubdir1\n\t\tfile1.ext\n\t\tsubsubdir1\n\tsubdir2\n\t\tsubsubdir2\n\t\t\tfile2.ext
        //\n 换行符，表示另起一个文件或文件夹
        //\t 制表符，表示当前文件的层级
        int n = input.length();
        int pos = 0;
        int ans = 0;
        int[] level = new int[n + 1];
        while (pos < n) {
            //当前文件或文件夹的深度
            int depth = 1;
            //根据/t的数量确定深度
            while (pos < n && input.charAt(pos) == '\t') {
                depth++;
                pos++;
            }
            //当前文件或文件夹的长度
            int length = 0;
            boolean isFile = false;
            //到下一个换行符之前，确定文件或文件夹的长度
            while (pos < n && input.charAt(pos) != '\n') {
                if (input.charAt(pos) == '.') {
                    isFile = true;
                }
                pos++;
                length++;
            }

            //当前文件或文件夹的总长度
            if (depth > 1) {
                length += level[depth - 1] + 1;
            }

            if (isFile) {
                ans = Math.max(ans, length);
            } else {
                //当前目录的深度
                level[depth] = length;
            }
            //跳过换行符
            pos++;
        }

        return ans;

    }


    /**
     * L386
     * 386. 字典序排数
     * 给你一个整数 n ，按字典序返回范围 [1, n] 内所有整数。
     * <p>
     * 你必须设计一个时间复杂度为 O(n) 且使用 O(1) 额外空间的算法。
     * <p>
     * 示例 1：
     * <p>
     * 输入：n = 13
     * 输出：[1,10,11,12,13,2,3,4,5,6,7,8,9]
     * 示例 2：
     * <p>
     * 输入：n = 2
     * 输出：[1,2]
     *
     * @param n
     * @return
     */
    public List<Integer> lexicalOrder(int n) {
        //37 1 10 11 12 19
        // 2
        //
        int length = String.valueOf(n).length();
        travel("", length, n, 0);
        return result;
    }

    List<Integer> result = new ArrayList<>();

    private void travel(String pre, int length, int n, int index) {
        if (index >= length) {
            return;
        }

        for (char temp = (index == 0 ? '1' : '0'); temp <= '9'; temp++) {
            String cur = pre + temp;
            int curInt = Integer.parseInt(cur);
            if (curInt <= n) {
                result.add(Integer.parseInt(cur));
            }
            travel(cur, length, n, index + 1);
        }
    }


    public List<Integer> lexicalOrderV2(int n) {
        //37 1 10 11 12 19
        // 2
        //
        for (int i = 1; i <= 9; i++) {
            travelV2(n, i);
        }
        return result;
    }

    private void travelV2(int n, int cur) {
        if (cur > n) {
            return;
        }
        result.add(cur);
        for (int i = 0; i <= 9; i++) {
            travelV2(n, cur * 10 + i);
        }
    }


}

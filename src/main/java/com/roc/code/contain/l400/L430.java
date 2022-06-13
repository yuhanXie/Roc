package com.roc.code.contain.l400;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * @author gang.xie
 */
public class L430 {

    public static void main(String[] args) {
//        输入：start = "AACCGGTT", end = "AACCGGTA", bank = ["AACCGGTA"]
//        输出：1
//        示例 2：
//
//        输入：start = "AACCGGTT", end = "AAACGGTA", bank = ["AACCGGTA","AACCGCTA","AAACGGTA"]
//        输出：2
//        示例 3：
//
//        输入：start = "AAAAACCC", end = "AACCCCCC", bank = ["AAAACCCC","AAACCCCC","AACCCCCC"]
//        输出：3
        L430 l430 = new L430();
//        int result = l430.minMutation("AACCGGTT", "AACCGGTA", new String[]{"AACCGGTA"});
//        int result = l430.minMutation("AACCGGTT", "AAACGGTA", new String[]{"AACCGGTA","AACCGCTA","AAACGGTA"});
//        int result = l430.minMutation("AAAAACCC", "AACCCCCC", new String[]{"AAAACCCC", "AAACCCCC", "AACCCCCC"});
//        System.out.println(result);
//        int[] result1 = l430.findRightIntervalV2(new int[][]{{1, 2}});
//        System.out.println(Arrays.toString(result1));
//        int[] result2 = l430.findRightIntervalV2(new int[][]{{3, 4}, {2, 3}, {1, 2}});
//        System.out.println(Arrays.toString(result2));
//        int[] result3 = l430.findRightIntervalV2(new int[][]{{1, 4}, {2, 3}, {3, 4}});
//        System.out.println(Arrays.toString(result3));
        int[] result4 = l430.findRightIntervalV2(new int[][]{{4, 5}, {2, 3}, {1, 2}});
        System.out.println(Arrays.toString(result4));
    }


    /**
     * L433 最小基因变化
     *
     * @param start
     * @param end
     * @param bank
     * @return
     */
    public int minMutation(String start, String end, String[] bank) {
        //广度搜索
        if (start.equals(end)) {
            return 0;
        }
        HashSet<String> set = new HashSet<>(Arrays.asList(bank));
        if (!set.contains(end)) {
            return -1;
        }
        char[] charArray = new char[]{'A', 'C', 'G', 'T'};
        Queue<String> queue = new LinkedList<>();
        HashSet<String> visited = new HashSet<>();
        visited.add(start);
        queue.offer(start);
        int step = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int k = 0; k < size; k++) {
                String cur = queue.poll();
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 4; j++) {
                        if (charArray[j] != cur.charAt(i)) {
                            StringBuilder stringBuilder = new StringBuilder(cur);
                            stringBuilder.setCharAt(i, charArray[j]);
                            String replace = stringBuilder.toString();
                            if (replace.equals(end)) {
                                return step + 1;
                            }
                            if (set.contains(replace)) {
                                queue.offer(replace);
                            }
                        }
                    }
                }
            }
            step++;
        }
        return -1;

    }

    /**
     * L436 寻找右区间
     * 每个 starti 都 不同
     *
     * @param intervals
     * @return
     */
    public int[] findRightInterval(int[][] intervals) {
        //暴力法 n^2
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < intervals.length; i++) {
            map.put(intervals[i][0], i);
        }

        // 12 23 34
        // 13 26 45 57
        Arrays.sort(intervals, Comparator.comparingInt(o -> o[0]));
        int[] result = new int[intervals.length];
        Arrays.fill(result, -1);
        for (int i = 0; i < intervals.length; i++) {
            for (int j = i + 1; j < intervals.length; j++) {
                if (intervals[i][1] <= intervals[j][0]) {
                    result[map.get(intervals[i][0])] = map.get(intervals[j][0]);
                    break;
                }
            }
        }
        return result;
    }

    public int[] findRightIntervalV2(int[][] intervals) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < intervals.length; i++) {
            map.put(intervals[i][0], i);
        }
        Arrays.sort(intervals, Comparator.comparingInt(o -> o[0]));
        int[] result = new int[intervals.length];
        Arrays.fill(result, -1);
        //二分查找 12 23 45
        for (int i = 0; i < intervals.length; i++) {
            int left = i;
            int right = intervals.length - 1;
            while (left < right) {
                int mid = (left + right) >> 1;
                if (intervals[i][1] == intervals[mid][0]) {
                    result[map.get(intervals[i][0])] = map.get(intervals[mid][0]);
                    break;
                }
                if (intervals[i][1] < intervals[mid][0]) {
                    right = mid;
                } else {
                    left = mid + 1;
                }
            }
            if (result[map.get(intervals[i][0])] < 0
                    && intervals[i][1] <= intervals[right][0]) {
                result[map.get(intervals[i][0])] = map.get(intervals[right][0]);
            }

        }
        return result;
    }

    public int[] findRightIntervalV3(int[][] intervals) {
        //双指针
        int n = intervals.length;
        //左边界
        int[][] fs = new int[n][2];
        //右边界
        int[][] es = new int[n][2];
        for (int i = 0; i < n; i++) {
            fs[i] = new int[]{intervals[i][0], i};
            es[i] = new int[]{intervals[i][1], i};
        }
        Arrays.sort(fs, Comparator.comparingInt(o -> o[0]));
        Arrays.sort(es, Comparator.comparingInt(o -> o[0]));
        int[] result = new int[n];
        int j = 0;
        //遍历数组的右边界，找到第一个大于其的左边界
        for (int i = 0; i < n; i++) {
            //左 < 右
            while (j < n && es[i][0] > fs[j][0]) {
                j++;
            }
            //右边<=左边
            result[es[i][1]] = j == n ? -1 : fs[j][1];
        }
        return result;
    }
}

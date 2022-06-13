package com.roc.code.contain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * @author xiegang
 */
public class L390 {

    public L390() {
    }

    public static void main(String[] args) {
        L390 l390 = new L390();
        System.out.println(l390.maxRotateFunction(new int[]{4, 3, 2, 6}));
        System.out.println(l390.maxRotateFunctionV2(new int[]{4, 3, 2, 6}));
    }


//    public boolean isRectangleCover(int[][] rectangles) {
//
//    }



//    public int integerReplacement(int n) {
//
//    }


    /**
     * L396. 旋转函数
     *
     * @param nums
     * @return
     */
    public int maxRotateFunction(int[] nums) {
        //暴力法
        int max = 0;
        int length = nums.length;
        if (length <= 1) {
            return 0;
        }
        for (int i = 0; i < length; i++) {
            int index = 0;
            int temp = 0;
            int j = i;
            while (index < nums.length) {
                temp += nums[j % length] * index;
                index++;
                j++;
            }
            max = Math.max(max, temp);
        }
        return max;
    }


    /**
     * 动态规划
     *
     * @param nums nums
     * @return maxRotateFunctionV2
     */
    public int maxRotateFunctionV2(int[] nums) {
        // 动态规划
        //f(0) = 0 * arr[0] + 1 * arr[1] + ... + (n - 2) * arr[n - 2] + (n - 1) * arr[n - 1]
        //f(1) = 1 * arr[0] + 2 * arr[1] + ... +                        (n - 1) * arr[n - 2] + 0 * arr[n - 1]
        //f(2) = 2 * arr[0] + 3 * arr[1] + ... + (n - 1) * arr[n - 3] + 0 * arr[n - 2] + 1 * arr[n - 1]
        //f(1) - f(0) = arr[0] + arr[1] + ... + arr[n - 2] + arr[n - 1]  - n * arr[n - 1]
        //f(1) - f(0) = sum - n * arr[n -1]
        //f(2) - f(1) = sum - n * arr[n-2]
        //f(k) = f(k - 1) + sum - n * arr[n-k]

        int length = nums.length;
        if (length <= 1) {
            return 0;
        }
        int sum = Arrays.stream(nums).sum();
        int first = 0;
        for (int i = 0; i < length; i++) {
            first += i * nums[i];
        }
        int result = first;
        for (int i = 1; i< length; i++) {
            first = first + sum - length * nums[length - i];
            result = Math.max(result, first);
        }

        return result;

    }


    private HashMap<Integer, List<Integer>> map = new HashMap<>();

    private Random random = new Random();


    /**
     * L398 使用hashmap保存
     *
     * @param nums
     */
//    public L390(int[] nums) {
//        for (int i = 0; i < nums.length; i++) {
//            List<Integer> list = map.getOrDefault(nums[i], new ArrayList<>());
//            list.add(i);
//            map.put(nums[i], list);
//        }
//    }
//
//    public int pick(int target) {
//        List<Integer> list = map.get(target);
//
//        int temp = random.nextInt(list.size());
//        return list.get(temp);
//
//    }

    private int[] nums;

    /**
     * L398 蓄水池
     *
     * @param nums
     */
    public L390(int[] nums) {
        this.nums = nums;
    }

    public int pick(int target) {
        int count = 0;
        int result = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == target) {
                count++;
                if (random.nextInt(count) == 0) {
                    result = i;
                }
            }
        }
        return result;
    }


}

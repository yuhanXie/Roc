package com.roc.code.contain;

import java.util.Random;

public class L380 {


    class Solution {

        private int[] origin;
        private Random random = new Random();

        public Solution(int[] nums) {
            origin = nums;
        }


        public int[] reset() {
//            System.arraycopy(origin, 0, nums, 0, origin.length);
            return origin;
        }

        public int[] shuffle() {
            int[] result = origin.clone();
            for (int i = 0; i < result.length; i++) {
                //当前元素和后面的元素随机调换
                int j = i + random.nextInt(result.length - i);
                int temp = result[i];
                result[i] = result[j];
                result[j] = temp;
            }
            return result;
        }
    }
}

package com.roc.code.contain;

/**
 * @author gang.xie
 */
public class L700 {


    /**
     * L704 二分查找
     *
     * @param nums
     * @param target
     * @return
     */
    public int search(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;
        //note:left < right 或者 left <= right
        //可以通过边界值来判断，二分查找为例，假设数组只有一个元素，
        // 若没有等于的话，直接不进入循环，return -1了
        while (left <= right) {
            int mid = (left + right) >> 1;
            if (nums[mid] == target) {
                return mid;
            }
            if (nums[mid] < target) {
                // note 边界赋值问题，有些场景下 边界赋值，mid + 1或mid - 1也需要注意，
                // 否则有可能会进入死循环或者边界值每判断到
                //以二分为例，nums[mid]！= target了，那么就可以舍弃此边界
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }
}

package com.roc.code.contain.l400;

import com.roc.code.utils.LeetCodeUtils;
import com.roc.code.utils.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author gang.xie
 */
public class L440 {

    public static void main(String[] args) {
        L440 l440 = new L440();
//        List<Integer> result = l440.findDuplicates(new int[]{4,3,2,4,8,2,3,7});
//        result.forEach(System.out::println);

        System.out.println(l440.serialize(LeetCodeUtils.convert(new Integer[]{2,1,3})));
    }


    /**
     * L442 数组中重复的数据
     * 输入：nums = [4,3,2,4,8,2,3,7]
     * 输入：nums = [4,3,2,1,8,2,3,7]
     * 输出：[2,3]
     *
     * @param nums
     * @return
     */
    public List<Integer> findDuplicates(int[] nums) {
        //原地交换，相同元素不交换
        for (int i = 0; i < nums.length; i++) {
            while (nums[i] != nums[nums[i] - 1]) {
                int temp = nums[nums[i] - 1];
                nums[nums[i] - 1] = nums[i];
                nums[i] = temp;
            }
        }
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != i + 1) {
                result.add(nums[i]);
            }
        }
        return result;
    }


    public List<Integer> findDuplicatesV2(int[] nums) {
        //使用一次循环，不交换值，只把对应位置上的值变为负数
        //如果第二次发现是负数，则说明重复了
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            int temp = Math.abs(nums[i]);
            if (nums[temp - 1] < 0) {
                result.add(temp);
            } else {
                nums[temp - 1] = -nums[temp - 1];
            }
        }
        return result;
    }

    /**
     * L449 二叉搜索树序列化
     *
     * @param root
     * @return
     */
    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {
        List<Integer> list = new ArrayList<>();
        postOrder(root, list);
        String str = list.toString();
        return str.substring(1, str.length() - 1);
    }

    private void postOrder(TreeNode root, List<Integer> list) {
        if (root == null) {
            return;
        }
        postOrder(root.left, list);
        postOrder(root.right, list);
        list.add(root.val);
    }



    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        //2 3 5 7 6 4
        if ("".equals(data)) {
            return null;
        }
        String[] array = data.split(",");
        Stack<Integer> stack = new Stack<>();

        for (String s : array) {
            stack.add(Integer.parseInt(s));
        }
        return construct(Integer.MIN_VALUE, Integer.MAX_VALUE, stack);
    }

    private TreeNode construct(int minValue, int maxValue, Stack<Integer> queue) {
        if (queue.isEmpty() || queue.peek() < minValue || queue.peek() > maxValue) {
            return null;
        }
        int val = queue.pop();
        TreeNode root = new TreeNode(val);
        //顺序不能换，先构造右子树
        root.right = construct(val, maxValue, queue);
        root.left = construct(minValue, val, queue);
        return root;
    }
}

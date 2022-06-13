package com.roc.code.contain;


import com.roc.code.utils.TreeNode;
import com.roc.code.utils.LeetCodeUtils;

public class L560 {

    public static void main(String[] args) {
        L560 l560 = new L560();
        System.out.println(l560.findTilt(LeetCodeUtils.convert(new Integer[]{1, 2, 3})));
        //15
        System.out.println(l560.findTilt(LeetCodeUtils.convert(new Integer[]{4, 2, 9, 3, 5, null, 7})));
        //9
        System.out.println(l560.findTilt(LeetCodeUtils.convert(new Integer[]{21, 7, 14, 1, 1, 2, 2, 3, 3})));
    }

    public int findTilt(TreeNode root) {
        //后序遍历，记录左右节点的和
        result = 0;
        backtrack(root);
        return result;

    }

    int result = 0;

    private int backtrack(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int left = backtrack(root.left);
        int right = backtrack(root.right);
        result += Math.abs(left - right);
        return left + right + root.val;
    }
}

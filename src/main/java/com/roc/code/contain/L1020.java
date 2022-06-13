package com.roc.code.contain;

import com.roc.code.utils.LeetCodeUtils;
import com.roc.code.utils.TreeNode;

import java.util.Stack;

/**
 * @author gang.xie
 */
public class L1020 {

    public static void main(String[] args) {
        L1020 l1020 = new L1020();
//        System.out.println(l1020.sumRootToLeaf(LeetCodeUtils.convert(new Integer[]{1, 0, 1, 0, 1, 0, 1})));
        System.out.println(l1020.sumRootToLeaf(
                LeetCodeUtils.convert(new Integer[]{0, 1, 0, 0, null, 0, 0, null, null, null, 1, null, null, null, 1})));
    }


    /**
     * L1021
     *
     * @param s
     * @return
     */
    public String removeOuterParentheses(String s) {
        Stack<Character> stack = new Stack<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char curChar = s.charAt(i);
            if (curChar == ')') {
                stack.pop();
            }
            if (!stack.isEmpty()) {
                sb.append(curChar);
            }
            if (curChar == '(') {
                stack.push(curChar);
            }
        }
        return sb.toString();

    }

    /**
     * L1022 从根到叶的二进制数之和
     *
     * @param root
     * @return
     */
    public int sumRootToLeaf(TreeNode root) {
        if (root == null) {
            return 0;
        }
        preOrder(root, 0);
        return result;
    }

    private int result = 0;

    private void preOrder(TreeNode root, int pre) {
        pre = pre * 2 + root.val;
        if (root.left == null && root.right == null) {
            result += pre;
            return;
        }

        if (root.left != null) {
            preOrder(root.left, pre);
        }
        if (root.right != null) {
            preOrder(root.right, pre);
        }
    }
}

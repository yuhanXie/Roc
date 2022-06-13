package com.roc.code.contain.l400;

import com.roc.code.utils.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author gang.xie
 */
public class L0406 {


    /**
     * L面试题 04.06. 后继者
     * 设计一个算法，找出二叉搜索树中指定节点的“下一个”节点（也即中序后继）。
     *
     * @param root
     * @param p
     * @return
     */
    public TreeNode inorderSuccessor(TreeNode root, TreeNode p) {
        //迭代法遍历
        if (root == null) {
            return null;
        }
        Stack<TreeNode> stack = new Stack<>();
        TreeNode cur = root;
        TreeNode pre = null;
        while (!stack.isEmpty() || cur != null) {
            while (cur != null) {
                stack.push(cur);
                cur = cur.left;
            }
            cur = stack.pop();
            if (pre != null && p.val == pre.val) {
                return cur;
            }
            pre = cur;
            cur = cur.right;
        }
        return null;

    }

    /**
     * 递归
     *
     * @param root
     * @param p
     * @return
     */
    public TreeNode inorderSuccessorV2(TreeNode root, TreeNode p) {
        midOrder(root);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == p.val) {
                return i + 1 >= list.size() ? null : new TreeNode(list.get(i + 1));
            }
        }
        return null;
    }

    private List<Integer> list = new ArrayList<>();

    private void midOrder(TreeNode root) {
        if (root == null) {
            return;
        }
        midOrder(root.left);
        list.add(root.val);
        midOrder(root.right);
    }


    public TreeNode inorderSuccessorV3(TreeNode root, TreeNode p) {
        if (root == null) {
            return null;
        }
        if (root.val <= p.val) {
            return inorderSuccessorV3(root.right, p);
        }
        TreeNode result = inorderSuccessorV3(root.left, p);
        return result == null ? root : result;
    }

}

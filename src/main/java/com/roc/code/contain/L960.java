package com.roc.code.contain;

import com.roc.code.utils.TreeNode;

/**
 * @author gang.xie
 */
public class L960 {

    /**
     * L965
     *
     * @param root
     * @return
     */
    public boolean isUnivalTree(TreeNode root) {
        int val = root.val;
        return backtrack(root, val);
    }

    private boolean backtrack(TreeNode root, int val) {
        if (root.val != val) {
            return false;
        }
        if (root.left != null) {
            boolean left = backtrack(root.left, val);
            if (!left) {
                return false;
            }
        }
        if (root.right != null) {
            boolean right = backtrack(root.right, val);
            if (!right) {
                return false;
            }
        }
        return true;
    }


}

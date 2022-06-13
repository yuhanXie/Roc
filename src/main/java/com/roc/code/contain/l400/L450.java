package com.roc.code.contain.l400;

import com.roc.code.utils.LeetCodeUtils;
import com.roc.code.utils.TreeNode;

/**
 * @author gang.xie
 */
public class L450 {


    public static void main(String[] args) {
        L450 l450 = new L450();
        TreeNode result = l450.deleteNode(LeetCodeUtils.convert(new Integer[]{5, 3, 6, 2, 4, null, 7}), 3);
        TreeNode result1 = l450.deleteNode(LeetCodeUtils.convert(new Integer[]{5, 3, 6, 2, 4, null, 7}), 0);
        System.out.println(result.toString());
        System.out.println(result1.toString());
    }


    /**
     * L450. 删除二叉搜索树中的节点
     *
     * @param root
     * @param key
     * @return
     */
    public TreeNode deleteNode(TreeNode root, int key) {
        //先遍历查找，没找到，直接返回
        //找到之后删除当前节点后让右节点替换，如果没有右节点的话，就父节点连接左节点
        if (root == null) {
            return null;
        }
        if (root.val == key) {
            //删除当前
            //右节点为空的话，左节点为空则为叶子节点，返回null，左节点不为空，返回左节点即可
            if (root.right == null) {
                return root.left;
            }
            //右节点不为空，左节点为空的话，返回右节点
            if (root.left == null) {
                return root.right;
            }
            //左右节点都不为空，两种方式：1.找右子树的最小值 2.找左子树的最大值
//            TreeNode temp = root.right;
//            while (temp.left != null) {
//                temp = temp.left;
//            }
//            root.right = deleteNode(root.right, temp.val);
//            root.val = temp.val;
            TreeNode temp = root.left;
            while (temp.right != null) {
                temp = temp.right;
            }
            root.left = deleteNode(root.left, temp.val);
            root.val = temp.val;
            return root;
        }
        if (root.val < key) {
            root.right = deleteNode(root.right, key);
            return root;
        }

        root.left = deleteNode(root.left, key);
        return root;


    }


}

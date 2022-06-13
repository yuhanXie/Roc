package com.roc.code.contain;

import com.roc.code.utils.LeetCodeUtils;
import com.roc.code.utils.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author xiegang
 */
public class L290 {

    public static void main(String[] args) {
        L290 l290 = new L290();
        //= "1807", guess = "7810"
        //1123", guess = "0111"
        //"1", guess = "0"
        //1,1
//        System.out.println(l290.getHint("1807", "7810"));
//        System.out.println(l290.getHint("1123", "0111"));
//        System.out.println(l290.getHint("1", "0"));
//        System.out.println(l290.getHint("1", "1"));
        String result = l290.serialize(LeetCodeUtils.convert(new Integer[]{1, 2, 3, null, null, 4, 5}));
//        String result = l290.serialize(null);
        TreeNode root = l290.deserialize(result);
        System.out.println(result);
    }


    /**
     * L299. 猜数字游戏
     *
     * @param secret
     * @param guess
     * @return
     */
    public String getHint(String secret, String guess) {
        //如果相同的，则a+1，不同的话，map记录数字和数量，对比一下。
        // 长度相同
        int aCount = 0;
        int bCount = 0;
        HashMap<Character, Integer> sMap = new HashMap<>();
        HashMap<Character, Integer> gMap = new HashMap<>();
        for (int i = 0; i < secret.length(); i++) {
            char sChar = secret.charAt(i);
            char gChar = guess.charAt(i);
            if (sChar == gChar) {
                aCount++;
            } else {
                sMap.put(sChar, sMap.getOrDefault(sChar, 0) + 1);
                gMap.put(gChar, gMap.getOrDefault(gChar, 0) + 1);
            }
        }

        for (Character key : sMap.keySet()) {
            Integer sCount = sMap.get(key);
            Integer gCount = gMap.getOrDefault(key, 0);
            bCount += Math.min(sCount, gCount);
        }

        return aCount + "A" + bCount + "B";

    }

    /**
     * L297 二叉树的序列化与反序列化
     *
     * @param root
     * @return
     */
    //广度优先搜索 1,2,3,null,null,4,5
    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {
        List<Integer> list = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode cur = queue.poll();
            if (cur == null) {
                list.add(null);
            } else {
                list.add(cur.val);
                queue.add(cur.left);
                queue.add(cur.right);
            }
        }
        return list.toString();
    }

    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        String trim = data.substring(1, data.length() - 1);
        if ("null".equals(trim)) {
            return null;
        }
        String[] array = trim.split(", ");
        TreeNode root = new TreeNode(Integer.parseInt(array[0]));
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int index = 1;
        while (!queue.isEmpty()) {
            TreeNode cur = queue.poll();
            if (!"null".equals(array[index])) {
                TreeNode left = new TreeNode(Integer.parseInt(array[index]));
                cur.left = left;
                queue.add(left);
            }
            index++;
            if (!"null".equals(array[index])) {
                TreeNode right = new TreeNode(Integer.parseInt(array[index]));
                cur.right = right;
                queue.add(right);
            }
            index++;
        }
        return root;
    }


}

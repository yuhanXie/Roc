package com.roc.code.contain;

import com.roc.code.ListNode;

/**
 * @author xiegang
 */
public class L230 {


    /**
     * L237. 删除链表中的节点
     *
     * 请编写一个函数，用于 删除单链表中某个特定节点 。
     * 在设计函数时需要注意，你无法访问链表的头节点 head ，只能直接访问 要被删除的节点 。
     *
     * 题目数据保证需要删除的节点 不是末尾节点 。
     *
     * @param node
     */
    public void deleteNode(ListNode node) {
        //这真的是脑筋急转弯，一般删除节点，都会要求不能改变节点的值，但这个没有。那就很简单了
        node.val = node.next.val;
        node.next = node.next.next;
    }
}

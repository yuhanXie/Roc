package com.roc.code.contain;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author gang.xie
 */
public class L930 {

    public static void main(String[] args) {
        L930 l930 = new L930();
    }


    public L930() {
        this.timeQueue = new LinkedList<>();
    }

    private Queue<Integer> timeQueue;

    /**
     * L933
     *
     * @param t
     * @return
     */
    public int ping(int t) {
        //收到t之后，返回t-3000到t的请求数量
        timeQueue.offer(t);
        while (!timeQueue.isEmpty() && t - timeQueue.peek() > 3000) {
            timeQueue.poll();
        }
        return timeQueue.size();
    }
}

package com.roc.java.common;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author gang.xie
 */
public class ReentrantLockTest {

    private static ReentrantLock lock = new ReentrantLock(true);

    private static Condition condition = lock.newCondition();

    private int i;

    public void run() {
        String name = Thread.currentThread().getName();
        try {
            lock.lock();
            while (i < 100) {
                i++;
                System.out.println(name + ":" + i);
                condition.signal();
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            lock.unlock();
        }


    }

    public static void main(String[] args) {
        ReentrantLockTest test = new ReentrantLockTest();
        Runnable runnable = test::run;
        new Thread(runnable).start();
        new Thread(runnable).start();
    }

    /*


     */
}

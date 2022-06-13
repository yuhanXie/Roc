package com.roc.java.threalocal;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author gang.xie
 */
public class ThreadLocalTest {

    private static ThreadLocal<Object> threadLocal = new ThreadLocal<>();

    private static ThreadLocal<String> threadLocal2 = new ThreadLocal<>();

    public static void main(String[] args) {
        ThreadLocalTest test = new ThreadLocalTest();
//        test.test();
        test.testDirtyData();
    }

    public void test() {
        threadLocal.set("current thread value");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Thread thread1 = new Thread(() -> {
            System.out.println("thread1 print:" + threadLocal.get());
            threadLocal.set("thread1 value");
            threadLocal2.set("thread1 thread local2");
            countDownLatch.countDown();
        }, "thread1");
        thread1.start();
        try {
            //等待thread1执行结束
            countDownLatch.await();
            System.out.println("current thread print: " + threadLocal.get().toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void testDirtyData() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 10,
                TimeUnit.HOURS, new LinkedBlockingDeque<>());
        Runnable runnable = () -> {
            String data = threadLocal2.get();
            System.out.println(Thread.currentThread().getName() + ":" + data);
            threadLocal2.set("set value");
        };
        for (int i = 0; i < 3; i++) {
            executor.execute(runnable);
        }
    }
}

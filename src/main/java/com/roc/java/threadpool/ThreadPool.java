package com.roc.java.threadpool;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xiegang
 */
public class ThreadPool {


    public static void main(String[] args) {
//        UUID uuid = UUID.randomUUID();
//        System.out.println(uuid);
//        ExecutorService executorService1 = Executors.newCachedThreadPool();
//        ExecutorService executorService = new ThreadPoolExecutor(10, 10, 60,
//                TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        System.out.println(Integer.toBinaryString(-1));
        System.out.println(Integer.toBinaryString(-1 << 29));
        System.out.println(Integer.toBinaryString(0 << 29));
        System.out.println(Integer.toBinaryString(1 << 29));
        System.out.println(Integer.toBinaryString(2 << 29));
        System.out.println(Integer.toBinaryString(3 << 29));
        System.out.println(Integer.toBinaryString((1 << 29) - 1));

        System.out.println(Integer.toBinaryString((-1 << 29) & ((1 << 29) - 1) ));
    }

    public void ThreadPool() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 60, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(), new ThreadPoolExecutor.DiscardOldestPolicy());
        Executors.newCachedThreadPool();
        Executors.newSingleThreadExecutor();
        Executors.newFixedThreadPool(3);
        Executors.newScheduledThreadPool(2);
    }
}

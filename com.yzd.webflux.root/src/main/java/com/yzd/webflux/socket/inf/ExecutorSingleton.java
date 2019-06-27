package com.yzd.webflux.socket.inf;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 通过线程池的方式解决线程回收问题
 */
public class ExecutorSingleton {
    private static ExecutorSingleton ourInstance = new ExecutorSingleton();

    public static ExecutorSingleton getInstance() {
        return ourInstance;
    }

    private ExecutorSingleton() {
    }
    //线程池最大10000；
    private ExecutorService threadPoolExecutor = new ThreadPoolExecutor(0, 10000, 5L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

    public ExecutorService getThreadPool() {
        return threadPoolExecutor;
    }
}

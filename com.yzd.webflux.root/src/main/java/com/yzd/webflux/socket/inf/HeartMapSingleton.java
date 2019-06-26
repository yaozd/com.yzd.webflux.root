package com.yzd.webflux.socket.inf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class HeartMapSingleton {
    private static HeartMapSingleton ourInstance = new HeartMapSingleton();

    public static HeartMapSingleton getInstance() {
        return ourInstance;
    }

    private HeartMapSingleton() {
    }

    private ConcurrentHashMap<String, AtomicInteger> count = new ConcurrentHashMap<>();

    public Map<String, AtomicInteger> getCount() {
        return count;
    }

}

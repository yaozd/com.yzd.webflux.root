package com.yzd.webflux.socket.inf;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class HeartMapSingleton {
    private static HeartMapSingleton ourInstance = new HeartMapSingleton();

    public static HeartMapSingleton getInstance() {
        return ourInstance;
    }

    private HeartMapSingleton() {
    }

    private Map<String, AtomicInteger> count = new HashMap<>();

    public Map<String, AtomicInteger> getCount() {
        return count;
    }

}

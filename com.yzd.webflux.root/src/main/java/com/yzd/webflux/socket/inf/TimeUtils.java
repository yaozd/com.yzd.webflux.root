package com.yzd.webflux.socket.inf;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class TimeUtils {
    /**
     * 倒计时
     *
     * @param endTime
     *            倒计时间(毫秒)
     * @param timerTask
     *            实现
     */
    public static Timer countDown(
            int endTime,
            Consumer<Timer> timerTask) {
        // 开始时间
        long start = System.currentTimeMillis();
        // 结束时间
        long end = start + endTime;

        Timer timer = new Timer();
        // 计时结束时候，停止全部timer计时计划任务
        timer.schedule(new TimerTask() {
            public void run() {
                timerTask.accept(timer);
                timer.cancel();
            }
        }, new Date(end));
        return timer;

    }
}


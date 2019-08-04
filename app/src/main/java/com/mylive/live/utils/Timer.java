package com.mylive.live.utils;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

/**
 * Create by zailongshi on 2019/8/4
 */
public final class Timer {

    private final static class TimersHolder {
        private final static LinkedList<WeakReference<Timer>> TIMERS = new LinkedList<>();
    }

    public static Timer start() {
        if (TimersHolder.TIMERS.size() > 0) {
            WeakReference<Timer> timerRef = TimersHolder.TIMERS.removeFirst();
            Timer timer = timerRef.get();
            if (timer != null) {
                timer.startTime = System.currentTimeMillis();
                return timer;
            }
        }
        return new Timer(System.currentTimeMillis());
    }

    public static void recycle(Timer timer) {
        if (timer != null) {
            TimersHolder.TIMERS.add(new WeakReference<>(timer));
            timer = null;
        }
    }

    private long startTime;

    private Timer(long startTime) {
        this.startTime = startTime;
    }

    public long end() {
        return System.currentTimeMillis() - startTime;
    }
}

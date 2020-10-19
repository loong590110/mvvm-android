package com.mylive.live.utils;

import android.util.Log;

public final class TimeRuler {
    private static long start;
    private static long record;

    public static void start() {
        start = record = System.currentTimeMillis();
    }

    public static void measure(String tag) {
        long now = System.currentTimeMillis();
        long delta = now - record;
        record = now;
        Log.i("TimeRuler", tag + ": " + delta + "ms");
    }

    public static void end() {
        long delta = record - start;
        Log.i("TimeRuler", "total time: " + delta + "ms");
    }
}

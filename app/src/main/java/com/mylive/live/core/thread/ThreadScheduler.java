package com.mylive.live.core.thread;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Create by zailongshi on 2019/6/22
 */
public final class ThreadScheduler {

    private static class UiThreadHandler {
        private static Handler INSTANCE = new Handler(Looper.getMainLooper());
    }

    private static class IoTreadExecutor {
        private static Executor INSTANCE = Executors.newFixedThreadPool(5);
    }

    private static class NewTreadExecutor {
        private static Executor INSTANCE = Executors.newCachedThreadPool();
    }

    public static void runOnUiThread(Runnable runnable) {
        UiThreadHandler.INSTANCE.post(runnable);
    }

    public static void runOnIoThread(Runnable runnable) {
        IoTreadExecutor.INSTANCE.execute(runnable);
    }

    public static void runOnNewThread(Runnable runnable) {
        NewTreadExecutor.INSTANCE.execute(runnable);
    }
}

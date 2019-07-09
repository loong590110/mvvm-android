package com.mylive.live.component;

/**
 * Created by Developer Zailong Shi on 2019-07-09.
 */
public final class DoubleClickExit {

    private static class DoubleClickExitHolder {
        private static final DoubleClickExit INSTANCE = new DoubleClickExit();
    }

    public static DoubleClickExit getInstance() {
        return DoubleClickExitHolder.INSTANCE;
    }

    private long lastBackPressedTime;
    private long intervalMillsTime = 1000;//默认1秒

    private DoubleClickExit() {
    }

    /**
     * 在主Activity的onBackPressed重写方法中使用
     *
     * @return 返回true表示满足退出应用条件
     */
    public boolean onBackPressed() {
        long now = System.currentTimeMillis();
        if (now - lastBackPressedTime < intervalMillsTime) {
            return true;
        }
        lastBackPressedTime = now;
        return false;
    }

    public void setIntervalMillsTime(long intervalMillsTime) {
        this.intervalMillsTime = intervalMillsTime;
    }
}

package com.mylive.live.component;

import android.database.Observable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Create by zailongshi on 2019/7/8
 */
public class ScrollEvent extends Observable<ScrollEvent.Observer> {

    private Handler handler = new Handler(Looper.getMainLooper());
    @Direction
    private int latestDirection, currentDirection;
    private boolean ignoreDirection;

    public void notifyObservers(@Direction int direction) {
        latestDirection = direction;
        if (ignoreDirection) {
            return;
        }
        if (currentDirection == direction) {
            return;
        }
        ignoreDirection = true;
        for (Observer mObserver : mObservers) {
            handler.post(() -> mObserver.onChanged(direction));
        }
    }

    public void onScrolled(int dy) {
        if (Math.abs(dy) > 0) {
            @Direction int direction = dy > 0 ?
                    Direction.UP : Direction.DOWN;
            notifyObservers(direction);
        }
    }

    public void onFeedBack(@Direction int currentDirection) {
        this.ignoreDirection = false;
        this.currentDirection = currentDirection;
        if (currentDirection != latestDirection) {
            notifyObservers(latestDirection);
        }
    }

    public interface Observer {
        void onChanged(@Direction int direction);
    }

    @IntDef({Direction.UP, Direction.DOWN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Direction {
        int UP = 1, DOWN = -1;
    }
}

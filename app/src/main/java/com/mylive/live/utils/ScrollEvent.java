package com.mylive.live.utils;

import android.database.Observable;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;

/**
 * 本实现一个实例对象只能同时处理一个滚动方向，以y轴滚动为范例，
 * 也可以当做任何方向的滚动处理，只需在各自实例中隔离使用即可。
 * <p>
 * Created by Developer Zailong Shi on 2019-07-08.
 */
public class ScrollEvent extends Observable<ScrollEvent.Observer> {

    private Handler handler = new Handler(Looper.getMainLooper());
    @Direction
    private int latestDirection, currentDirection;
    private boolean ignoreDirection;
    private Filter filter;
    private Check check;

    /**
     * 向所有观察者发送方向值
     *
     * @param direction 被发送给观察者的方向值
     */
    public void notifyObservers(@Direction int direction) {
        if (mObservers.size() <= 0) {
            return;
        }
        if (filter != null && !filter.onFilter()) {
            return;
        }
        latestDirection = direction;
        if (ignoreDirection) {
            return;
        }
        if (check != null && check.onCheck(direction)) {
            currentDirection = direction;
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

    /**
     * 在滚动控件的滚动监听中使用，将滚动距离传入以待处理。
     *
     * @param dy y轴方向的滚动距离
     */
    public void onScrolled(int dy) {
        if (Math.abs(dy) > 0) {
            @Direction int direction = dy > 0 ?
                    Direction.UP : Direction.DOWN;
            notifyObservers(direction);
        }
    }

    /**
     * 在观察者收到方向值处理完成后必须通过本方法反馈处理结果，
     * 否则不再收到下一个方向值。
     *
     * @param currentDirection 本次处理的方向值
     */
    public void onFeedBack(@Direction int currentDirection) {
        this.currentDirection = currentDirection;
        this.ignoreDirection = false;
        if (currentDirection != latestDirection) {
            notifyObservers(latestDirection);
        }
    }

    /**
     * 过滤器，保留符合条件的方向值，结果为true表示符合,
     * 继续向下传递方向值，否则拦截，不再向下传递。
     *
     * @param filter 每次收到方向值时回调
     * @param <R>    返回本方法所属的对象
     * @return 同"<R>"解释
     */
    public <R extends ScrollEvent> R filter(Filter filter) {
        Objects.requireNonNull(filter);
        this.filter = filter;
        return (R) this;
    }

    /**
     * 预校验，检查发送来的方向和UI的目前方向是否一致,
     * 返回true表示一致，则拦截该方向值，不再向下传递。
     *
     * @param check 每次在观察者接收前回调
     * @param <R>   返回本方法所属的对象
     * @return 同"<R>"解释
     */
    public <R extends ScrollEvent> R check(Check check) {
        Objects.requireNonNull(check);
        this.check = check;
        return (R) this;
    }

    public interface Observer {
        void onChanged(@Direction int direction);
    }

    public interface Filter {
        boolean onFilter();
    }

    public interface Check {
        boolean onCheck(@Direction int direction);
    }

    @IntDef({Direction.UP, Direction.DOWN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Direction {
        int UP = 1, DOWN = -1;
    }
}

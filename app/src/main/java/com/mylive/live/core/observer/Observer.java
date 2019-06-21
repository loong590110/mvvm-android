package com.mylive.live.core.observer;

/**
 * Created by Developer Zailong Shi on 2019-06-21.
 */
public interface Observer<T> {
    void onChanged(T t);
}

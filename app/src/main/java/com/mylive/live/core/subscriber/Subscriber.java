package com.mylive.live.core.subscriber;

/**
 * Created by Developer Zailong Shi on 2019-06-19.
 */
public interface Subscriber<T> {
    void onPublish(T event);
}

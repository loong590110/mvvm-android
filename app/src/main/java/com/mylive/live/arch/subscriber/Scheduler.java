package com.mylive.live.arch.subscriber;

/**
 * Created by Developer Zailong Shi on 2019-06-19.
 */
public interface Scheduler extends Publisher {
    <T> Scheduler subscribe(Class<T> eventType, Subscriber<T> subscriber);

    @SuppressWarnings("UnusedReturnValue")
    <T> Scheduler unsubscribe(Class<T> eventType, Subscriber<T> subscriber);

    @SuppressWarnings("UnusedReturnValue")
    Scheduler unsubscribeAll();
}

package com.mylive.live.core.subscriber;

/**
 * Created by Developer Zailong Shi on 2019-06-19.
 */
public interface Publisher {
    <T> void publish(T event);
}

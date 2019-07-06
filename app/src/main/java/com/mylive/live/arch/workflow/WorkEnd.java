package com.mylive.live.arch.workflow;

/**
 * Create by zailongshi on 2019/7/6
 */
public interface WorkEnd<T> {
    void onEnd(T parcel);
}

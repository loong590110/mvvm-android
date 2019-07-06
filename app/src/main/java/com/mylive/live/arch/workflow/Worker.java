package com.mylive.live.arch.workflow;

/**
 * Create by zailongshi on 2019/6/22
 */
public interface Worker<R, P> {
    R doWork(P parcel);
}

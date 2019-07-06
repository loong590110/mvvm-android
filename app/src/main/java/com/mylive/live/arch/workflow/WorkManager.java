package com.mylive.live.arch.workflow;

/**
 * Create by zailongshi on 2019/6/22
 */
public interface WorkManager<T> {
    <R> WorkManager<R> deliver(Worker<R, T> worker);

    <R> void end(WorkEnd<R> workEnd);

    void end();
}

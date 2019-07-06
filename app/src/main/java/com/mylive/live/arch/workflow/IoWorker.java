package com.mylive.live.arch.workflow;

import java.util.Objects;

/**
 * Create by zailongshi on 2019/6/22
 */
public class IoWorker<R, P> implements Worker<R, P> {

    private Worker<R, P> worker;

    public static <R, P> IoWorker<R, P> work(Worker<R, P> worker) {
        return new IoWorker<>(worker);
    }

    private IoWorker(Worker<R, P> worker) {
        Objects.requireNonNull(worker);
        this.worker = worker;
    }

    @Override
    public R doWork(P parcel) {
        return worker.doWork(parcel);
    }
}

package com.mylive.live.arch.workflow;

import java.util.Objects;

/**
 * Create by zailongshi on 2019/6/22
 */
public class BackgroundWorker<R, P> implements Worker<R, P> {

    private Worker<R, P> worker;

    public BackgroundWorker() {
    }

    public BackgroundWorker(Worker<R, P> worker) {
        Objects.requireNonNull(worker);
        this.worker = worker;
    }

    @Override
    public R doWork(P parcel) {
        if (worker != null) {
            return worker.doWork(parcel);
        }
        return null;
    }
}

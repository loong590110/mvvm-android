package com.mylive.live.arch.workflow;

import java.util.Objects;

/**
 * Create by zailongshi on 2019/6/22
 */
public class UiWorker<R, P> implements Worker<R, P> {

    private Worker<R, P> worker;

    public UiWorker() {
    }

    public UiWorker(Worker<R, P> worker) {
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

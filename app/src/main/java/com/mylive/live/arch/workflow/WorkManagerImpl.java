package com.mylive.live.arch.workflow;

import com.mylive.live.arch.thread.ThreadsScheduler;

import java.util.Objects;

/**
 * Create by zailongshi on 2019/7/5
 */
public class WorkManagerImpl<T> implements WorkManager<T> {

    private WorkManager<?> workManager;
    private Worker<?, T> worker;
    private T parcel;

    public WorkManagerImpl() {
    }

    public WorkManagerImpl(T parcel) {
        this.parcel = parcel;
    }

    public WorkManagerImpl(WorkManager<?> workManager) {
        this.workManager = workManager;
    }

    @Override
    public <R> WorkManager<R> deliver(Worker<R, T> worker) {
        Objects.requireNonNull(worker);
        this.worker = worker;
        return new WorkManagerImpl<>(this);
    }

    @Override
    public void end(WorkEnd<T> workEnd) {
        if (workManager != null) {
            workManager.end(parcel1 -> {
                this.parcel = (T) parcel1;
                doWork(workEnd);
            });
            return;
        }
        doWork(workEnd);
    }

    @Override
    public void end() {
        end(null);
    }

    private <R> void doWork(WorkEnd<R> workEnd) {
        if (worker instanceof UiWorker) {
            ThreadsScheduler.runOnUiThread(() -> {
                workEnd.onEnd((R) worker.doWork(parcel));
            });
        } else if (worker instanceof IoWorker) {
            ThreadsScheduler.runOnIoThread(() -> {
                workEnd.onEnd((R) worker.doWork(parcel));
            });
        } else if (worker instanceof BackgroundWorker) {
            ThreadsScheduler.runOnNewThread(() -> {
                workEnd.onEnd((R) worker.doWork(parcel));
            });
        } else if (worker != null) {
            /*
             * 在不指定执行线程的情况下，直接在上一个流程的线程中工作
             */
            workEnd.onEnd((R) worker.doWork(parcel));
        } else {
            /*
             *  如果worker == null，说明是最后一个流程，
             *  并且workEnd != null的情况，
             *  把最终结果直接抛到UI线程里，
             *  方便用户直接操作UI控件,
             *  而不引起程序异常。
             */
            if (workEnd != null) {
                ThreadsScheduler.runOnUiThread(() -> {
                    workEnd.onEnd((R) parcel);
                });
            }
        }
    }
}

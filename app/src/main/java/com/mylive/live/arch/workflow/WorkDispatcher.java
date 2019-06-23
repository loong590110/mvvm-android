package com.mylive.live.arch.workflow;

import com.mylive.live.arch.thread.ThreadScheduler;

import java.util.Queue;

/**
 * Create by zailongshi on 2019/6/22
 */
final class WorkDispatcher {

    private Queue<Worker> workerQueue;

    WorkDispatcher(Queue<Worker> workerQueue) {
        this.workerQueue = workerQueue;
    }

    void dispatch() {
        doWork(new Parcel());
    }

    private void doWork(Parcel parcel) {
        if (workerQueue.isEmpty())
            return;
        Worker work = workerQueue.poll();
        if (work != null) {
            Runnable runnable = () -> doWork(work.doWork(parcel));
            if (work instanceof UiWorker) {
                ThreadScheduler.runOnUiThread(runnable);
            } else if (work instanceof IoWorker) {
                ThreadScheduler.runOnIoThread(runnable);
            } else if (work instanceof BackgroundWorker) {
                ThreadScheduler.runOnNewThread(runnable);
            } else {
                runnable.run();
            }
        }
    }
}

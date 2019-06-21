package com.mylive.live.core.workflow;

import com.mylive.live.core.thread.ThreadScheduler;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Create by zailongshi on 2019/6/22
 */
public final class WorkFlow {

    private Queue<Work> workQueue;

    private WorkFlow(Queue<Work> workQueue) {
        this.workQueue = workQueue;
    }

    public static WorkFlow begin() {
        return new WorkFlow(new LinkedBlockingQueue<>());
    }

    public WorkFlow addWork(Work work) {
        workQueue.add(work);
        return this;
    }

    public void end() {
        doWork(new Data());
    }

    private void doWork(Data data) {
        if (workQueue.isEmpty())
            return;
        Work work = workQueue.poll();
        if (work != null) {
            Runnable runnable = () -> doWork(work.doWork(data));
            if (work instanceof UiWork) {
                ThreadScheduler.runOnUiThread(runnable);
            } else if (work instanceof IoWork) {
                ThreadScheduler.runOnIoThread(runnable);
            } else if (work instanceof BackgroundWork) {
                ThreadScheduler.runOnNewThread(runnable);
            } else {
                runnable.run();
            }
        }
    }
}

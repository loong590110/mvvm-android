package com.mylive.live.arch.workflow;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Create by zailongshi on 2019/6/22
 */
public final class WorkFlow {

    private Queue<Worker> workerQueue;

    private WorkFlow(Queue<Worker> workerQueue) {
        this.workerQueue = workerQueue;
    }

    public static WorkFlow begin() {
        return new WorkFlow(new LinkedBlockingQueue<>());
    }

    public WorkFlow deliver(Worker worker) {
        workerQueue.add(worker);
        return this;
    }

    public void end() {
        new WorkDispatcher(workerQueue).dispatch();
    }
}

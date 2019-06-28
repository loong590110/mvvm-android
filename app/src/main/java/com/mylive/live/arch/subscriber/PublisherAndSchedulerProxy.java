package com.mylive.live.arch.subscriber;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Developer Zailong Shi on 2019-06-28.
 */
public class PublisherAndSchedulerProxy implements Scheduler {

    private Map<Class, Subscriber> subscribers = new ConcurrentHashMap<>();
    private Scheduler publisherAndScheduler;

    public PublisherAndSchedulerProxy(Scheduler scheduler) {
        this.publisherAndScheduler = scheduler;
    }

    @Override
    public <T> Scheduler subscribe(Class<T> eventType, Subscriber<T> subscriber) {
        if (!subscribers.containsKey(eventType)) {
            subscribers.put(eventType, subscriber);
            publisherAndScheduler.subscribe(eventType, subscriber);
        }
        return this;
    }

    @Override
    public <T> Scheduler unsubscribe(Class<T> eventType, Subscriber<T> subscriber) {
        publisherAndScheduler.unsubscribe(eventType, subscriber);
        return this;
    }

    @Override
    public Scheduler unsubscribeAll() {
        for (Map.Entry<Class, Subscriber> entry : subscribers.entrySet()) {
            //noinspection unchecked
            publisherAndScheduler.unsubscribe(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public <T> void publish(T event) {
        publisherAndScheduler.publish(event);
    }
}

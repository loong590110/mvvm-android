package com.mylive.live.arch.mvvm;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.mylive.live.arch.exception.ProhibitedException;
import com.mylive.live.arch.subscriber.Scheduler;
import com.mylive.live.arch.subscriber.Subscriber;
import com.mylive.live.arch.subscriber.SubscribesScheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Developer Zailong Shi on 2019-06-19.
 */
@SuppressLint("Registered")
public class CommunicableActivity extends FragmentActivity implements LifecycleObserver {

    private static class SchedulerHolder {
        private static final Scheduler scheduler = new SubscribesScheduler();
    }

    private Scheduler schedulerAndPublisherProxy = new Scheduler() {

        private Map<Class, Subscriber> subscribers = new ConcurrentHashMap<>();

        @Override
        public <T> Scheduler subscribe(Class<T> eventType, Subscriber<T> subscriber) {
            if (!subscribers.containsKey(eventType)) {
                subscribers.put(eventType, subscriber);
                SchedulerHolder.scheduler.subscribe(eventType, subscriber);
            }
            return this;
        }

        @Override
        public <T> Scheduler unsubscribe(Class<T> eventType, Subscriber<T> subscriber) {
            SchedulerHolder.scheduler.unsubscribe(eventType, subscriber);
            return this;
        }

        @Override
        public Scheduler unsubscribeAll() {
            for (Map.Entry<Class, Subscriber> entry : subscribers.entrySet()) {
                //noinspection unchecked
                SchedulerHolder.scheduler.unsubscribe(entry.getKey(), entry.getValue());
            }
            return this;
        }

        @Override
        public <T> void publish(T event) {
            SchedulerHolder.scheduler.publish(event);
        }
    };

    {
        getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onSubscribe() {
        onSubscribe(schedulerAndPublisherProxy);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onUnsubscribe() {
        schedulerAndPublisherProxy.unsubscribeAll();
    }

    protected void onSubscribe(Scheduler scheduler) {

    }

    protected <T> void publish(T event) {
        schedulerAndPublisherProxy.publish(event);
    }

    @Deprecated
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        throw new ProhibitedException("Please start activity by extends ActivityStarter class.");
    }

    @Deprecated
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        throw new ProhibitedException("Please start activity by extends ActivityStarter class.");
    }
}

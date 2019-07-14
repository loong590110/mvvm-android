package com.mylive.live.arch.mvvm;

import android.annotation.SuppressLint;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.mylive.live.arch.feature.FeaturesActivity;
import com.mylive.live.arch.subscriber.PublisherAndSchedulerProxy;
import com.mylive.live.arch.subscriber.Scheduler;
import com.mylive.live.arch.subscriber.SubscribesScheduler;

/**
 * Created by Developer Zailong Shi on 2019-06-19.
 */
@SuppressLint("Registered")
public class CommunicableActivity extends FeaturesActivity implements LifecycleObserver {

    public static final String SCHEDULER_HOLDER_CLASSNAME = SchedulerHolder.class.getName();

    private static final class SchedulerHolder {
        private static final Scheduler SCHEDULER = new SubscribesScheduler();
    }

    private final Scheduler schedulerAndPublisherProxy = new PublisherAndSchedulerProxy(
            CommunicableActivity.SchedulerHolder.SCHEDULER
    );

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
}

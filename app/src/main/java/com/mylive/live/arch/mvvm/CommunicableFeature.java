package com.mylive.live.arch.mvvm;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import com.mylive.live.arch.feature.Feature;
import com.mylive.live.arch.feature.FeaturesManagerOwner;
import com.mylive.live.arch.subscriber.PublisherAndSchedulerProxy;
import com.mylive.live.arch.subscriber.Scheduler;

import java.lang.reflect.Field;

/**
 * Created by Developer Zailong Shi on 2019-06-28.
 */
public class CommunicableFeature extends Feature {

    private Scheduler schedulerAndPublisherProxy;

    public CommunicableFeature(FeaturesManagerOwner owner) {
        super(owner);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onSubscribe() {
        try {
            String classname = CommunicableActivity.SCHEDULER_HOLDER_CLASSNAME;
            Class<?> schedulerHolderClass = Class.forName(classname);
            Field[] fields = schedulerHolderClass.getDeclaredFields();
            for (Field field : fields) {
                if (Scheduler.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    Scheduler publisherAndScheduler = (Scheduler) field.get(null);
                    schedulerAndPublisherProxy = new PublisherAndSchedulerProxy(
                            publisherAndScheduler);
                    onSubscribe(schedulerAndPublisherProxy);
                    break;
                }
            }
        } catch (IllegalAccessException ignore) {
        } catch (ClassNotFoundException ignore) {
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onUnsubscribe() {
        if (schedulerAndPublisherProxy != null) {
            schedulerAndPublisherProxy.unsubscribeAll();
        }
    }

    protected void onSubscribe(Scheduler scheduler) {

    }

    protected <T> void publish(T event) {
        if (schedulerAndPublisherProxy != null) {
            schedulerAndPublisherProxy.publish(event);
        }
    }
}

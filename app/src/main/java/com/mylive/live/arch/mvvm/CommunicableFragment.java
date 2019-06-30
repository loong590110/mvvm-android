package com.mylive.live.arch.mvvm;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.mylive.live.arch.exception.ProhibitedException;
import com.mylive.live.arch.subscriber.PublisherAndSchedulerProxy;
import com.mylive.live.arch.subscriber.Scheduler;

import java.lang.reflect.Field;

/**
 * Created by Developer Zailong Shi on 2019-06-28.
 */
public class CommunicableFragment extends Fragment implements LifecycleObserver {

    private Scheduler schedulerAndPublisherProxy;

    {
        getLifecycle().addObserver(this);
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

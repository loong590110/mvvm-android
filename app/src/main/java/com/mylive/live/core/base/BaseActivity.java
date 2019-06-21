package com.mylive.live.core.base;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.SparseArray;

import com.mylive.live.core.subscriber.Scheduler;
import com.mylive.live.core.subscriber.Subscriber;
import com.mylive.live.exception.ProhibitedException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Developer Zailong Shi on 2019-06-19.
 */
@SuppressLint("Registered")
public class BaseActivity extends FragmentActivity implements LifecycleObserver {

    private static volatile Scheduler scheduler = new Scheduler() {

        private SparseArray<List<Subscriber>> subscribers = new SparseArray<>();
        private Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public <T> Scheduler subscribe(Class<T> eventType, Subscriber<T> subscriber) {
            int eventTypeCode = getEventTypeCode(eventType);
            if (eventTypeCode > 0) {
                List<Subscriber> subscribers = this.subscribers.get(eventTypeCode);
                if (subscribers == null) {
                    subscribers = new CopyOnWriteArrayList<>();
                }
                if (!subscribers.contains(subscriber)) {
                    subscribers.add(subscriber);
                }
                this.subscribers.put(eventTypeCode, subscribers);
            }
            return this;
        }

        @Override
        public <T> Scheduler unsubscribe(Class<T> eventType, Subscriber<T> subscriber) {
            int eventTypeCode = getEventTypeCode(eventType);
            if (eventTypeCode > 0) {
                List<Subscriber> subscribers = this.subscribers.get(eventTypeCode);
                if (subscribers != null) {
                    subscribers.remove(subscriber);
                }
            }
            return this;
        }

        @Override
        public Scheduler unsubscribeAll() {
            subscribers.clear();
            return this;
        }

        @Override
        public <T> void publish(T event) {
            if (event == null)
                return;
            int eventTypeCode = getEventTypeCode(event.getClass());
            List<Subscriber> subscribers = this.subscribers.get(eventTypeCode);
            if (subscribers != null && subscribers.size() > 0) {
                for (Subscriber subscriber : subscribers) {
                    //noinspection unchecked
                    handler.post(() -> subscriber.onPublish(event));
                }
            }
        }

        private <T> int getEventTypeCode(Class<T> eventType) {
            return eventType.hashCode();
        }
    };

    private Scheduler schedulerProxy = new Scheduler() {

        private Map<Class, Subscriber> subscribers = new ConcurrentHashMap<>();

        @Override
        public <T> Scheduler subscribe(Class<T> eventType, Subscriber<T> subscriber) {
            if (!subscribers.containsKey(eventType)) {
                subscribers.put(eventType, subscriber);
                scheduler.subscribe(eventType, subscriber);
            }
            return this;
        }

        @Override
        public <T> Scheduler unsubscribe(Class<T> eventType, Subscriber<T> subscriber) {
            scheduler.unsubscribe(eventType, subscriber);
            return this;
        }

        @Override
        public Scheduler unsubscribeAll() {
            for (Map.Entry<Class, Subscriber> entry : subscribers.entrySet()) {
                //noinspection unchecked
                scheduler.unsubscribe(entry.getKey(), entry.getValue());
            }
            return this;
        }

        @Override
        public <T> void publish(T event) {
            scheduler.publish(event);
        }
    };

    {
        getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onSubscribe() {
        onSubscribe(schedulerProxy);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onUnsubscribe() {
        schedulerProxy.unsubscribeAll();
    }

    protected void onSubscribe(Scheduler scheduler) {

    }

    protected <T> void publish(T event) {
        schedulerProxy.publish(event);
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

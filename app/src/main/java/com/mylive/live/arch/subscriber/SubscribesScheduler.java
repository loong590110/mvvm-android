package com.mylive.live.arch.subscriber;

import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Create by zailongshi on 2019/6/27
 */
public class SubscribesScheduler implements Scheduler {

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
}

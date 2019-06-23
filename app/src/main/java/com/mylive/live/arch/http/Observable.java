package com.mylive.live.arch.http;

import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;

import com.mylive.live.arch.observer.Observer;

/**
 * Created by Developer Zailong Shi on 2019-06-21.
 */
public interface Observable<T> extends com.mylive.live.arch.observer.Observable<T>,
        LifecycleObserver {
    void observe(Observer<T> observer, ObserverError<Throwable> observerError);

    <R> void observe(ObserverSuccess<R> observerSuccess);

    <R> void observe(ObserverSuccess<R> observerSuccess, ObserverError<Throwable> observerError);

    Observable<T> dispose(LifecycleOwner lifecycleOwner);
}

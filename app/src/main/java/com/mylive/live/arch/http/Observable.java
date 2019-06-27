package com.mylive.live.arch.http;

import android.arch.lifecycle.LifecycleOwner;

import com.mylive.live.arch.exception.HttpException;
import com.mylive.live.arch.thread.Scheduler;

/**
 * Created by Developer Zailong Shi on 2019-06-21.
 */
public interface Observable<T> {
    <R> void observe(ObserverSuccess<R> observerSuccess);

    <R> void observe(ObserverSuccess<R> observerSuccess, ObserverError<HttpException> observerError);

    Observable<T> dispose(LifecycleOwner lifecycleOwner);

    Observable<T> onObserve(ObserverDisposable<Disposable> observerDisposable);

    Observable<T> observeOn(Scheduler scheduler);

    Observable<T> observeOnUiThread();
}

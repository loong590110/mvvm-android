package com.mylive.live.arch.http;

import androidx.lifecycle.LifecycleOwner;

import com.mylive.live.arch.observer.Observer;
import com.mylive.live.arch.thread.Scheduler;

/**
 * Created by Developer Zailong Shi on 2019-06-21.
 */
public interface Observable<T> {
    <R> void observe(Observer<R> rObserver);

    <R> void observe(Observer<R> rObserver, Observer<HttpException> exceptionObserver);

    Observable<T> dispose(LifecycleOwner lifecycleOwner);

    Observable<T> onObserve(Observer<Disposable> disposableObserver);

    Observable<T> observeOn(Scheduler scheduler);

    Observable<T> observeOnUiThread();
}

package com.mylive.live.core.http;

import com.mylive.live.core.observer.Observer;

/**
 * Created by Developer Zailong Shi on 2019-06-21.
 */
public interface Observable<T> extends com.mylive.live.core.observer.Observable<T> {
    void observe(Observer<T> observer, ObserverError<Throwable> observerError);

    <R> void observe(ObserverSuccess<R> observerSuccess);

    <R> void observe(ObserverSuccess<R> observerSuccess, ObserverError<Throwable> observerError);
}

package com.mylive.live.interceptor;

import android.database.Observable;

import okhttp3.Interceptor;

/**
 * Created by Developer Zailong Shi on 2019-06-28.
 */
public abstract class ObservableInterceptor<T> extends Observable<T> implements Interceptor {
}

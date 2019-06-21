package com.mylive.live.core.http;

/**
 * Created by Developer Zailong Shi on 2019-06-21.
 */
public interface HttpResponse<T> {
    boolean isSuccessful();

    T getData();
}

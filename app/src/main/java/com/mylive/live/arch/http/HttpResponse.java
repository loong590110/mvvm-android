package com.mylive.live.arch.http;

/**
 * Created by Developer Zailong Shi on 2019-06-21.
 */
public interface HttpResponse<T> {
    boolean isSuccessful();

    int getCode();

    String getMessage();

    T getData();
}

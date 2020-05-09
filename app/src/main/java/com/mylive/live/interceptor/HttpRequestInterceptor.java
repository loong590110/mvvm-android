package com.mylive.live.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public class HttpRequestInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        return chain.proceed(rebuildRequest(chain.request()));
    }

    private Request rebuildRequest(Request request) {
        return new Request.Builder()
                .header("User-Agent", "PostmanRuntime/7.21.0")
                .method(request.method(), request.body())
                .url(request.url())
                .build();
    }
}

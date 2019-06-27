package com.mylive.live.interceptor;

import com.mylive.live.config.HttpConfig;

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
                .headers(request.headers())
                .header(HttpConfig.USER_AGENT[0], HttpConfig.USER_AGENT[1])
                .url(request.url())
                .build();
    }
}

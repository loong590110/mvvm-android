package com.mylive.live.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public class HttpResponseInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        //do something
        return response;
    }
}

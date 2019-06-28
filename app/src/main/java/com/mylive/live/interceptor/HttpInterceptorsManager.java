package com.mylive.live.interceptor;

/**
 * Created by Developer Zailong Shi on 2019-06-27.
 */
public final class HttpInterceptorsManager {

    private static class RequestInterceptorHolder {
        private static final HttpRequestInterceptor INSTANCE = new HttpRequestInterceptor();
    }

    private static class ResponseInterceptorHolder {
        private static final HttpResponseInterceptor INSTANCE = new HttpResponseInterceptor();
    }

    public static HttpRequestInterceptor getHttpRequestInterceptor() {
        return RequestInterceptorHolder.INSTANCE;
    }

    public static HttpResponseInterceptor getHttpResponseInterceptor() {
        return ResponseInterceptorHolder.INSTANCE;
    }
}

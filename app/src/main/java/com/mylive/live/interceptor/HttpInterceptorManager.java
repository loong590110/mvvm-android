package com.mylive.live.interceptor;

/**
 * Created by Developer Zailong Shi on 2019-06-27.
 */
public final class HttpInterceptorManager {

    private static class RequestInterceptorHolder {
        private static final HttpRequestInterceptor INSTANCE = new HttpRequestInterceptor();
    }

    private static class ResponseInterceptorHolder {
        private static final HttpResponseInterceptor INSTANCE = new HttpResponseInterceptor();
    }

    private static HttpRequestInterceptor getHttpRequestInterceptor() {
        return RequestInterceptorHolder.INSTANCE;
    }

    private static HttpResponseInterceptor getHttpResponseInterceptor() {
        return ResponseInterceptorHolder.INSTANCE;
    }
}

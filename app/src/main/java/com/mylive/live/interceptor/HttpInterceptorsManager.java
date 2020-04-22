package com.mylive.live.interceptor;

import com.itkacher.okhttpprofiler.OkHttpProfilerInterceptor;
import com.mylive.live.BuildConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Developer Zailong Shi on 2019-06-27.
 */
public final class HttpInterceptorsManager {

    private static class EmptyInterceptorHolder {
        private static final EmptyInterceptor INSTANCE = new EmptyInterceptor();
    }

    private static class EmptyProfilerInterceptorHolder {
        private static final EmptyProfilerInterceptor INSTANCE = new EmptyProfilerInterceptor();
    }

    private static class RequestInterceptorHolder {
        private static final HttpRequestInterceptor INSTANCE = new HttpRequestInterceptor();
    }

    private static class ResponseInterceptorHolder {
        private static final HttpResponseInterceptor INSTANCE = new HttpResponseInterceptor();
    }

    private static class ProfilerInterceptorHolder {
        private static final OkHttpProfilerInterceptor INSTANCE = new OkHttpProfilerInterceptor();
    }

    public static HttpRequestInterceptor getHttpRequestInterceptor() {
        return RequestInterceptorHolder.INSTANCE;
    }

    public static HttpResponseInterceptor getHttpResponseInterceptor() {
        return ResponseInterceptorHolder.INSTANCE;
    }

    public static OkHttpProfilerInterceptor getHttpProfilerInterceptor() {
        if (BuildConfig.DEBUG) {
            return ProfilerInterceptorHolder.INSTANCE;
        }
        return EmptyProfilerInterceptorHolder.INSTANCE;
    }

    private static class EmptyProfilerInterceptor extends OkHttpProfilerInterceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            return chain.proceed(chain.request());
        }
    }

    private static class EmptyInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            return chain.proceed(chain.request());
        }
    }
}

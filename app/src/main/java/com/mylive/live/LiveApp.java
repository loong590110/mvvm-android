package com.mylive.live;

import android.app.Application;

import com.mylive.live.arch.http.HttpConfigProvider;
import com.mylive.live.config.HttpConfig;
import com.mylive.live.interceptor.HttpRequestInterceptor;
import com.mylive.live.interceptor.HttpResponseInterceptor;

import okhttp3.OkHttpClient;

/**
 * Created by Developer Zailong Shi on 2019-06-19.
 */
public class LiveApp extends Application {

    private static Application context;

    public static Application getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        HttpConfigProvider.getConfig()
                .setBaseUrl(HttpConfig.BASE_URL)
                .setHttpClient(new OkHttpClient.Builder()
                        .addInterceptor(new HttpRequestInterceptor())
                        .addInterceptor(new HttpResponseInterceptor())
                        .build())
                .apply();
    }
}

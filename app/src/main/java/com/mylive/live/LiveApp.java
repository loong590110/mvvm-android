package com.mylive.live;

import android.app.Application;

import com.mylive.live.arch.http.HttpConfigProvider;
import com.mylive.live.config.HttpConfig;
import com.mylive.live.interceptor.HttpInterceptorsManager;

import okhttp3.OkHttpClient;

/**
 * Created by Developer Zailong Shi on 2019-06-19.
 */
public class LiveApp extends Application {

    private static LiveApp context;

    public static LiveApp getAppContext() {
        return (LiveApp) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        HttpConfigProvider.getConfig()
                .setBaseUrl(HttpConfig.BASE_URL)
                .setHttpClient(new OkHttpClient.Builder()
                        .addInterceptor(
                                HttpInterceptorsManager.getHttpRequestInterceptor())
                        .addInterceptor(
                                HttpInterceptorsManager.getHttpResponseInterceptor())
                        .build())
                .apply();
    }
}

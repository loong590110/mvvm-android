package com.mylive.live;

import android.app.Application;

import com.mylive.live.config.HttpConfig;
import com.mylive.live.core.http.Retrofit2;
import com.mylive.live.interceptor.HttpRequestInterceptor;

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
        Retrofit2.init(new Retrofit2.Config.Builder()
                .setDefaultBaseUrl(HttpConfig.BASE_URL)
                .setInterceptors(new HttpRequestInterceptor())
                .build());
    }
}

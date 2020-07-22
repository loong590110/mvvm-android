package com.mylive.live;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.mylive.live.arch.http.HttpConfigProvider;
import com.mylive.live.config.HttpConfig;
import com.mylive.live.imageloader.FrescoImageLoader;
import com.mylive.live.imageloader.ImageLoader;
import com.mylive.live.interceptor.HttpInterceptorsManager;
import com.mylive.live.ssl.SSLContextWrapper;

import org.apache.http.conn.ssl.SSLSocketFactory;

import okhttp3.OkHttpClient;

/**
 * Created by Developer Zailong Shi on 2019-06-19.
 */
public class LiveApp extends MultiDexApplication {

    public static LiveApp instance;

    public static LiveApp instance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //init http config
        HttpConfigProvider.getConfig()
                .setBaseUrl(HttpConfig.BASE_URL)
                .setHttpClient(new OkHttpClient.Builder()
                        //region: trust all crt.
                        .sslSocketFactory(
                                SSLContextWrapper.getInstance().getSocketFactory(),
                                SSLContextWrapper.getX509TrustManager()
                        )
                        .hostnameVerifier(
                                SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER
                        )
                        //endregion
                        .addInterceptor(
                                HttpInterceptorsManager.getHttpRequestInterceptor()
                        )
                        .addInterceptor(
                                HttpInterceptorsManager.getHttpResponseInterceptor()
                        )
                        .addInterceptor(
                                HttpInterceptorsManager.getHttpProfilerInterceptor()
                        )
                        .build())
                .apply();
        //init image loader
        ImageLoader.init(new ImageLoader.Config(new FrescoImageLoader(this)));
        Glide.get(this).clearMemory();
    }
}

package com.mylive.live;

import android.app.Application;

import com.itkacher.okhttpprofiler.OkHttpProfilerInterceptor;
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
public class LiveApp extends Application {

    public static LiveApp instance;

    public static LiveApp instance() {
        return instance;
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
    }
}

package com.mylive.live.arch.http;

import java.util.Objects;

import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Create by zailongshi on 2019/6/27
 */
public final class ServiceCreator {

    private static volatile Retrofit instance;

    private static Retrofit getInstance() {
        if (instance == null) {
            synchronized (ServiceCreator.class) {
                if (instance == null) {
                    Retrofit.Builder builder = new Retrofit.Builder()
                            .baseUrl(HttpConfigProvider.getConfig().getBaseUrl())
                            .client(HttpConfigProvider.getConfig().getHttpClient())
                            .addCallAdapterFactory(HttpConfigProvider.getConfig()
                                    .getCallAdapter());
                    for (Converter.Factory factory
                            : HttpConfigProvider.getConfig().getConverterFactoris()) {
                        builder.addConverterFactory(factory);
                    }
                    instance = builder.build();
                }
            }
        }
        return instance;
    }

    public static <T> T create(Class<T> service) {
        Objects.requireNonNull(service);
        return getInstance().create(service);
    }

    static void recycle() {
        instance = null;
    }
}

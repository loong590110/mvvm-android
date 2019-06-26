package com.mylive.live.arch.http;

import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Create by zailongshi on 2019/6/27
 */
public final class ServiceCreator {

    private static class RetrofitHolder {
        private static final Retrofit instance;

        static {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(HttpConfigProvider.getConfig().getBaseUrl())
                    .client(HttpConfigProvider.getConfig().getHttpClient())
                    .addCallAdapterFactory(HttpConfigProvider.getConfig().getCallAdapter());
            for (Converter.Factory factory
                    : HttpConfigProvider.getConfig().getConverterFactoris()) {
                builder.addConverterFactory(factory);
            }
            instance = builder.build();
        }
    }

    public static <T> T create(Class<T> service) {
        return RetrofitHolder.instance.create(service);
    }
}

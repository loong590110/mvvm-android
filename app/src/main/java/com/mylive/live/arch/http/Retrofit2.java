package com.mylive.live.arch.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public final class Retrofit2 {

    private static final String BASE_URL = "https://www.mylive.com/";

    private static volatile Retrofit defaultRetrofit;
    private static Map<String, Retrofit> retrofitMap;
    private static OkHttpClient httpClient;
    private static HttpCallAdapter.Factory callAdapter;
    private static FastJsonConverterFactory fastJsonConverterFactory;
    private static ScalarsConverterFactory scalarsConverterFactory;
    private static String defaultBaseUrl = BASE_URL;
    private static Interceptor[] interceptors;

    public static Retrofit getDefault() {
        if (defaultRetrofit == null) {
            synchronized (Retrofit2.class) {
                if (defaultRetrofit == null) {
                    defaultRetrofit = buildRetrofit(defaultBaseUrl);
                }
            }
        }
        return defaultRetrofit;
    }

    public static Retrofit getRetrofit(String baseUrl) {
        baseUrl = getSafeBaseUrl(baseUrl);
        if (baseUrl.equals(String.valueOf(getDefault().baseUrl()))) {
            return getDefault();
        }
        if (retrofitMap != null && retrofitMap.containsKey(baseUrl)) {
            Retrofit retrofit = retrofitMap.get(baseUrl);
            if (retrofit != null) {
                return retrofit;
            }
        }
        Retrofit retrofit = buildRetrofit(baseUrl);
        if (retrofitMap == null) {
            retrofitMap = new ConcurrentHashMap<>();
        }
        retrofitMap.put(baseUrl, retrofit);
        return retrofit;
    }

    public static void updateDefaultBaseUrl(String baseUrl) {
        baseUrl = getSafeBaseUrl(baseUrl);
        if (shouldUpdate(baseUrl)) {
            synchronized (Retrofit2.class) {
                if (shouldUpdate(baseUrl)) {
                    defaultBaseUrl = baseUrl;
                    defaultRetrofit = buildRetrofit(baseUrl);
                }
            }
        }
    }

    public static void init(Config config) {
        if (defaultRetrofit != null || retrofitMap != null) {
            throw new IllegalStateException("Please call init method before create retrofit instance.");
        }
        if (config == null)
            return;
        if (config.defaultBaseUrl != null) {
            defaultBaseUrl = config.defaultBaseUrl;
        }
        if (config.interceptors != null && config.interceptors.length > 0) {
            interceptors = config.interceptors;
        }
    }

    private static String getSafeBaseUrl(String baseUrl) {
        if (baseUrl == null) {
            baseUrl = "";
        }
        return baseUrl;
    }

    private static boolean shouldUpdate(String baseUrl) {
        return baseUrl != null && (defaultRetrofit == null
                || !baseUrl.equals(String.valueOf(defaultRetrofit.baseUrl())));
    }

    private static Retrofit buildRetrofit(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getHttpClient())
                .addCallAdapterFactory(getCallAdapter())
                .addConverterFactory(getScalarsConverterFactory())
                .addConverterFactory(getFastJsonConverterFactory())
                .build();
    }

    private static OkHttpClient getHttpClient() {
        if (httpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
            httpClient = builder.build();
        }
        return httpClient;
    }

    private static Converter.Factory getFastJsonConverterFactory() {
        if (fastJsonConverterFactory == null) {
            fastJsonConverterFactory = FastJsonConverterFactory.create();
        }
        return fastJsonConverterFactory;
    }

    private static Converter.Factory getScalarsConverterFactory() {
        if (scalarsConverterFactory == null) {
            scalarsConverterFactory = ScalarsConverterFactory.create();
        }
        return scalarsConverterFactory;
    }

    private static CallAdapter.Factory getCallAdapter() {
        if (callAdapter == null) {
            callAdapter = HttpCallAdapter.Factory.create();
        }
        return callAdapter;
    }

    public static final class Config {
        private String defaultBaseUrl;
        private Interceptor[] interceptors;

        private Config(String defaultBaseUrl, Interceptor... interceptors) {
            this.defaultBaseUrl = defaultBaseUrl;
            this.interceptors = interceptors;
        }

        public static final class Builder {
            private String defaultBaseUrl;
            private Interceptor[] interceptors;

            public Builder setDefaultBaseUrl(String defaultBaseUrl) {
                this.defaultBaseUrl = defaultBaseUrl;
                return this;
            }

            public Builder setInterceptors(Interceptor... interceptors) {
                this.interceptors = interceptors;
                return this;
            }

            public Config build() {
                return new Config(defaultBaseUrl, interceptors);
            }
        }
    }
}

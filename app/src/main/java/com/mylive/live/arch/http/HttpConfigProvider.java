package com.mylive.live.arch.http;

import java.util.Objects;

import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.converter.fastjson.FastJsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public final class HttpConfigProvider {

    private static volatile Config config;

    public static Config getConfig() {
        if (config == null) {
            synchronized (HttpConfigProvider.class) {
                if (config == null) {
                    config = new Config();
                }
            }
        }
        return config;
    }

    public static final class Config {
        private String baseUrl;
        private OkHttpClient httpClient;
        private HttpCallAdapter.Factory callAdapter;
        private Converter.Factory[] converterFactories;

        private Config() {
            httpClient = new OkHttpClient();
            callAdapter = HttpCallAdapter.Factory.create();
            converterFactories = new Converter.Factory[]{
                    ScalarsConverterFactory.create(),
                    FastJsonConverterFactory.create()
            };
        }

        public OkHttpClient getHttpClient() {
            return httpClient;
        }

        public Config setHttpClient(OkHttpClient httpClient) {
            Objects.requireNonNull(httpClient);
            this.httpClient = httpClient;
            return this;
        }

        public HttpCallAdapter.Factory getCallAdapter() {
            return callAdapter;
        }

        public Config setCallAdapter(HttpCallAdapter.Factory callAdapter) {
            Objects.requireNonNull(callAdapter);
            this.callAdapter = callAdapter;
            return this;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public Config setBaseUrl(String baseUrl) {
            Objects.requireNonNull(baseUrl);
            this.baseUrl = baseUrl;
            return this;
        }

        public Converter.Factory[] getConverterFactories() {
            return converterFactories;
        }

        public Config setConverterFactories(Converter.Factory... converterFactories) {
            Objects.requireNonNull(converterFactories);
            this.converterFactories = converterFactories;
            return this;
        }

        public void apply() {
            ServiceCreator.recycle();
        }
    }
}

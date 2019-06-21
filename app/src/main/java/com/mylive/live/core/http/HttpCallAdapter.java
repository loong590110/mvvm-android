package com.mylive.live.core.http;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public class HttpCallAdapter<R> implements CallAdapter<R, Observable<R>> {

    private Type responseType;

    private HttpCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Type responseType() {
        return responseType;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Observable<R> adapt(Call<R> call) {
        return new ObservableImpl<>(call);
    }

    public static class Factory extends CallAdapter.Factory {

        private Factory() {
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
            Class<?> rawType = getRawType(returnType);
            if (rawType != Observable.class) {
                return null;
            }
            Type responseType = getParameterUpperBound(0, (ParameterizedType) returnType);
            return new HttpCallAdapter(responseType);
        }

        static Factory create() {
            return new Factory();
        }
    }
}

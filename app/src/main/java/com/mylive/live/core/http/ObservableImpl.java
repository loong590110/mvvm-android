package com.mylive.live.core.http;

import android.os.Handler;
import android.os.Looper;

import com.mylive.live.core.observer.Observer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Developer Zailong Shi on 2019-06-21.
 */
public class ObservableImpl<T> implements Observable<T> {

    private final Call<T> originalCall;

    private static class HandlerHolder {
        private static Handler INSTANCE = new Handler(Looper.getMainLooper());
    }

    ObservableImpl(Call<T> originalCall) {
        this.originalCall = originalCall;
    }

    @Override
    public void observe(Observer<T> observer) {
        observe(observer, null);
    }

    @Override
    public void observe(Observer<T> observer, ObserverError<Throwable> observerError) {
        //noinspection NullableProblems
        originalCall.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                HandlerHolder.INSTANCE.post(() -> observer.onChanged(response.body()));
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                if (observerError != null) {
                    HandlerHolder.INSTANCE.post(() -> observerError.onChanged(t));
                }
            }
        });
    }

    @Override
    public <R> void observe(ObserverSuccess<R> observerSuccess) {
        observe(observerSuccess, null);
    }

    @Override
    public <R> void observe(ObserverSuccess<R> observerSuccess, ObserverError<Throwable> observerError) {
        //noinspection NullableProblems
        originalCall.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                ObservableImpl.this.onResponse(observerSuccess, observerError, response);
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                if (observerError != null) {
                    HandlerHolder.INSTANCE.post(() -> observerError.onChanged(t));
                }
            }
        });
    }

    private <R> void onResponse(ObserverSuccess<R> observerSuccess,
                                ObserverError<Throwable> observerError,
                                Response<T> response) {
        try {
            //noinspection unchecked
            HttpResponse<R> httpResponse = (HttpResponse<R>) response.body();
            //noinspection ConstantConditions
            if (httpResponse.isSuccessful()) {
                HandlerHolder.INSTANCE.post(() -> {
                    observerSuccess.onChanged(httpResponse.getData());
                });
            }
        } catch (Exception e) {
            if (observerError != null) {
                HandlerHolder.INSTANCE.post(() -> observerError.onChanged(e));
            }
        }
    }
}

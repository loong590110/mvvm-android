package com.mylive.live.arch.http;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;

import com.mylive.live.arch.observer.Observer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Developer Zailong Shi on 2019-06-21.
 */
public class ObservableImpl<T> implements Observable<T> {

    private final Call<T> originalCall;
    private boolean isDestroyed;
    private Lifecycle lifecycle;

    ObservableImpl(Call<T> originalCall) {
        this.originalCall = originalCall;
    }

    @Override
    public void observe(Observer<T> observer) {
        observe(observer, null);
    }

    @Override
    public void observe(Observer<T> observer, ObserverError<Throwable> observerError) {
        originalCall.enqueue(new CallbackProxy<T>() {
            @Override
            public void onResponse(Response<T> response) {
                observer.onChanged(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                if (observerError != null) {
                    observerError.onChanged(t);
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
        originalCall.enqueue(new CallbackProxy<T>() {
            @Override
            public void onResponse(Response<T> response) {
                ObservableImpl.this.onResponse(observerSuccess, observerError, response);
            }

            @Override
            public void onFailure(Throwable t) {
                if (observerError != null) {
                    observerError.onChanged(t);
                }
            }
        });
    }

    @Override
    public Observable<T> dispose(LifecycleOwner lifecycleOwner) {
        lifecycle = lifecycleOwner.getLifecycle();
        lifecycle.addObserver(this);
        return this;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        isDestroyed = true;
        originalCall.cancel();
        lifecycle.removeObserver(this);
        lifecycle = null;
    }

    private <R> void onResponse(ObserverSuccess<R> observerSuccess,
                                ObserverError<Throwable> observerError,
                                Response<T> response) {
        try {
            //noinspection unchecked
            HttpResponse<R> httpResponse = (HttpResponse<R>) response.body();
            //noinspection ConstantConditions
            if (httpResponse.isSuccessful()) {
                observerSuccess.onChanged(httpResponse.getData());
            }
        } catch (Exception e) {
            if (observerError != null) {
                observerError.onChanged(e);
            }
        }
    }

    @SuppressWarnings({"NullableProblems", "TypeParameterHidesVisibleType"})
    private abstract class CallbackProxy<T> implements Callback<T> {
        @Override
        public final void onResponse(Call<T> call, Response<T> response) {
            if (isDestroyed)
                return;
            onResponse(response);
        }

        @Override
        public final void onFailure(Call<T> call, Throwable t) {
            if (isDestroyed)
                return;
            onFailure(t);
        }

        public abstract void onResponse(Response<T> response);

        public abstract void onFailure(Throwable t);
    }
}

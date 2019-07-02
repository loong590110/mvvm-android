package com.mylive.live.arch.http;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;

import com.mylive.live.arch.exception.HttpException;
import com.mylive.live.arch.thread.Scheduler;
import com.mylive.live.arch.thread.ThreadsScheduler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Developer Zailong Shi on 2019-06-21.
 */
public class ObservableImpl<T> implements Observable<T>, LifecycleObserver {

    private final Call<T> originalCall;
    private boolean isDisposed;
    private Lifecycle lifecycle;
    private ObserverDisposable<Disposable> observerDisposable;
    private Scheduler scheduler;

    ObservableImpl(Call<T> originalCall) {
        this.originalCall = originalCall;
    }

    @Override
    public <R> void observe(ObserverSuccess<R> observerSuccess) {
        observe(observerSuccess, null);
    }

    @Override
    public <R> void observe(ObserverSuccess<R> observerSuccess,
                            ObserverError<HttpException> observerError) {
        observeActual(observerSuccess, observerError);
    }

    @Override
    public Observable<T> dispose(LifecycleOwner lifecycleOwner) {
        lifecycle = lifecycleOwner.getLifecycle();
        lifecycle.addObserver(this);
        return this;
    }

    @Override
    public Observable<T> onObserve(ObserverDisposable<Disposable> observerDisposable) {
        this.observerDisposable = observerDisposable;
        return this;
    }

    @Override
    public Observable<T> observeOn(Scheduler scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    @Override
    public Observable<T> observeOnUiThread() {
        this.scheduler = ThreadsScheduler.UiThreadScheduler();
        return this;
    }

    private <R> void observeActual(ObserverSuccess<R> observerSuccess,
                                   ObserverError<HttpException> observerError) {
        if (observerDisposable != null) {
            observerDisposable.onChanged(this::onDisposed);
        }
        originalCall.enqueue(new UniversalCallback<>(observerSuccess, observerError));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDisposed() {
        isDisposed = true;
        originalCall.cancel();
        lifecycle.removeObserver(this);
        lifecycle = null;
    }

    private class UniversalCallback<R> extends CallbackProxy<T> {
        private ObserverSuccess<R> observerSuccess;
        private ObserverError<HttpException> observerError;

        private UniversalCallback(ObserverSuccess<R> observerSuccess,
                                  ObserverError<HttpException> observerError) {
            this.observerSuccess = observerSuccess;
            this.observerError = observerError;
        }

        @Override
        public void onResponse(Response<T> response) {
            try {
                if (!response.isSuccessful()) {
                    throw new HttpException(response.code(), response.message());
                }
                if (observerSuccess != null) {
                    if (response.body() == null) {
                        throw new HttpException("Response body object is null.");
                    }
                    try {
                        //第一次直接返回根节点的结果，如果不匹配则尝试返回data节点
                        observerSuccess.onChanged((R) response.body());
                        return;
                    } catch (ClassCastException cce) {
                        if (response.body() instanceof HttpResponse) {
                            HttpResponse httpResponse = (HttpResponse) response.body();
                            try {
                                observerSuccess.onChanged((R) httpResponse.getData());
                                return;
                            } catch (ClassCastException e) {
                                cce = e;
                            }
                        }
                        throw new HttpException(cce);
                    }
                }
                throw new HttpException("ObserverSuccess object is null.");
            } catch (HttpException e) {
                if (observerError != null) {
                    observerError.onChanged(e);
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            if (observerError != null) {
                observerError.onChanged(new HttpException(t));
            }
        }
    }

    @SuppressWarnings({"NullableProblems", "TypeParameterHidesVisibleType"})
    private abstract class CallbackProxy<T> implements Callback<T> {

        private void schedule(Runnable runnable) {
            if (scheduler == null) {
                scheduler = Runnable::run;
            }
            scheduler.run(runnable);
        }

        @Override
        public final void onResponse(Call<T> call, Response<T> response) {
            if (isDisposed)
                return;
            schedule(() -> onResponse(response));
        }

        @Override
        public final void onFailure(Call<T> call, Throwable t) {
            if (isDisposed)
                return;
            schedule(() -> onFailure(t));
        }

        public abstract void onResponse(Response<T> response);

        public abstract void onFailure(Throwable t);
    }
}

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
    public void observe(ObserverResponse<T> observerResponse) {
        observe(observerResponse, null);
    }

    @Override
    public void observe(ObserverResponse<T> observerResponse,
                        ObserverError<HttpException> observerError) {
        observeActual(observerResponse, null, observerError);
    }

    @Override
    public <R> void observe(ObserverSuccess<R> observerSuccess) {
        observe(observerSuccess, null);
    }

    @Override
    public <R> void observe(ObserverSuccess<R> observerSuccess,
                            ObserverError<HttpException> observerError) {
        observeActual(null, observerSuccess, observerError);
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

    private <R> void observeActual(ObserverResponse<T> observerResponse,
                                   ObserverSuccess<R> observerSuccess,
                                   ObserverError<HttpException> observerError) {
        if (observerDisposable != null) {
            observerDisposable.onChanged(this::onDisposed);
        }
        originalCall.enqueue(new UniversalCallback<>(observerResponse,
                observerSuccess, observerError));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDisposed() {
        isDisposed = true;
        originalCall.cancel();
        lifecycle.removeObserver(this);
        lifecycle = null;
    }

    private class UniversalCallback<R> extends CallbackProxy<T> {
        private ObserverResponse<T> observerResponse;
        private ObserverSuccess observerSuccess;
        private ObserverError<HttpException> observerError;

        private UniversalCallback(ObserverResponse<T> observerResponse,
                                  ObserverSuccess<R> observerSuccess,
                                  ObserverError<HttpException> observerError) {
            this.observerResponse = observerResponse;
            this.observerSuccess = observerSuccess;
            this.observerError = observerError;
        }

        @Override
        public void onResponse(Response<T> response) {
            try {
                if (observerResponse != null) {
                    observerResponse.onChanged(response.body());
                    return;
                }
                if (observerSuccess != null) {
                    //noinspection unchecked
                    HttpResponse<R> httpResponse = (HttpResponse<R>) response.body();
                    //noinspection ConstantConditions
                    if (httpResponse.isSuccessful()) {
                        //noinspection unchecked
                        observerSuccess.onChanged(httpResponse.getData());
                        return;
                    }
                    throw new HttpException(httpResponse.getCode(), httpResponse.getMessage());
                }
                throw new HttpException("ObserverResponse and ObserverSuccess both are null.");
            } catch (Exception e) {
                if (observerError != null) {
                    observerError.onChanged(e instanceof HttpException ?
                            (HttpException) e : new HttpException(e));
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

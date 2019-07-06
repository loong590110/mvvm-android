package com.mylive.live.arch.http;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;

import com.mylive.live.arch.observer.Observer;
import com.mylive.live.arch.thread.Scheduler;
import com.mylive.live.arch.thread.ThreadsScheduler;
import com.mylive.live.arch.workflow.WorkManager;
import com.mylive.live.arch.workflow.Worker;

import java.lang.reflect.Field;

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
    private Observer<Disposable> disposableObserver;
    private Scheduler scheduler;

    ObservableImpl(Call<T> originalCall) {
        this.originalCall = originalCall;
    }

    @Override
    public <R> void observe(Observer<R> rObserver) {
        observe(rObserver, null);
    }

    @Override
    public <R> void observe(Observer<R> rObserver,
                            Observer<HttpException> exceptionObserver) {
        observeActual(rObserver, exceptionObserver);
    }

    @Override
    public Observable<T> dispose(LifecycleOwner lifecycleOwner) {
        lifecycle = lifecycleOwner.getLifecycle();
        lifecycle.addObserver(this);
        return this;
    }

    @Override
    public Observable<T> onObserve(Observer<Disposable> disposableObserver) {
        this.disposableObserver = disposableObserver;
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

    @Override
    public <R> WorkManager<R> deliver(Worker<R, T> worker,
                                      Worker<HttpException, T> exceptionTWorker) {
        return null;
    }

    private <R> void observeActual(Observer<R> rObserver,
                                   Observer<HttpException> exceptionObserver) {
        if (disposableObserver != null) {
            disposableObserver.onChanged(this::onDisposed);
        }
        originalCall.enqueue(new ResponseHandler<>(rObserver, exceptionObserver));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDisposed() {
        isDisposed = true;
        originalCall.cancel();
        lifecycle.removeObserver(this);
        lifecycle = null;
    }

    private class ResponseHandler<R> extends CallbackProxy<T> {
        private Observer<R> rObserver;
        private Observer<HttpException> exceptionObserver;
        private int code, STATUS_OK;
        private String desc;
        private R content;

        private ResponseHandler(Observer<R> rObserver,
                                Observer<HttpException> exceptionObserver) {
            this.rObserver = rObserver;
            this.exceptionObserver = exceptionObserver;
        }

        @Override
        public void onResponse(Response<T> response) {
            try {
                if (!response.isSuccessful()) {
                    throw new HttpException(response.code(), response.message());
                }
                if (rObserver != null) {
                    if (response.body() == null) {
                        throw new HttpException("Response body object is null.");
                    }
                    try {
                        //第一次直接返回根节点的结果，如果不匹配则尝试返回子节点
                        rObserver.onChanged((R) response.body());
                        return;
                    } catch (ClassCastException cce) {
                        Field[] fields = response.body().getClass().getDeclaredFields();
                        int count = 0;
                        for (Field field : fields) {
                            if (count == 3) {
                                break;
                            }
                            if (!field.isAccessible()) {
                                field.setAccessible(true);
                            }
                            if (field.isAnnotationPresent(HttpStatusCode.class)) {
                                count++;
                                try {
                                    STATUS_OK = field.getAnnotation(HttpStatusCode.class)
                                            .STATUS_OK();
                                    code = field.getInt(response.body());
                                } catch (IllegalAccessException e) {
                                    throw new HttpException(e);
                                } catch (ClassCastException e) {
                                    throw new HttpException("field " + field.getName()
                                            + " must be a Integer.");
                                }
                            } else if (field.isAnnotationPresent(HttpStatusDesc.class)) {
                                count++;
                                try {
                                    desc = (String) field.get(response.body());
                                } catch (IllegalAccessException e) {
                                    throw new HttpException(e);
                                } catch (ClassCastException e) {
                                    throw new HttpException("field " + field.getName()
                                            + " must be a String.");
                                }
                            } else if (field.isAnnotationPresent(HttpContent.class)) {
                                count++;
                                try {
                                    content = (R) field.get(response.body());
                                } catch (IllegalAccessException e) {
                                    throw new HttpException(e);
                                } catch (ClassCastException e) {
                                    throw new HttpException(e);
                                }
                            }
                        }
                        if (code == STATUS_OK) {
                            rObserver.onChanged(content);
                            return;
                        }
                        throw new HttpException(cce);
                    }
                }
                throw new HttpException("rObserver object is null.");
            } catch (HttpException e) {
                if (exceptionObserver != null) {
                    exceptionObserver.onChanged(e);
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            if (exceptionObserver != null) {
                exceptionObserver.onChanged(new HttpException(code, desc, t));
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

package com.mylive.live.base;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mylive.live.arch.mvvm.CommunicableActivity;
import com.mylive.live.arch.observer.Observer;
import com.mylive.live.arch.workflow.BackgroundWorker;
import com.mylive.live.arch.workflow.Parcel;
import com.mylive.live.arch.workflow.UiWorker;
import com.mylive.live.arch.workflow.WorkFlow;
import com.mylive.live.interceptor.HttpInterceptorsManager;
import com.mylive.live.model.HttpResponse;
import com.mylive.live.utils.ToastUtils;

/**
 * Created by Developer Zailong Shi on 2019-06-27.
 */
@SuppressLint("Registered")
public class BaseActivity extends CommunicableActivity {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void registerHttpInterceptorObserver() {
        HttpInterceptorsManager
                .getHttpResponseInterceptor()
                .registerObserver(httpResponseObserver);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void unregisterHttpInterceptorObserver() {
        HttpInterceptorsManager
                .getHttpResponseInterceptor()
                .unregisterObserver(httpResponseObserver);
    }

    private Observer<String> httpResponseObserver = respText -> {
        switch (getLifecycle().getCurrentState()) {
            case INITIALIZED:
            case DESTROYED:
                break;
            case CREATED:
                break;
            case STARTED:
                break;
            case RESUMED:
                WorkFlow.begin()
                        .deliver(new BackgroundWorker() {
                            @Override
                            public Parcel doWork(Parcel parcel) {
                                HttpResponse<String> httpResponse = JSON.parseObject(
                                        respText,
                                        new TypeReference<HttpResponse<String>>() {
                                        }.getType()
                                );
                                publish(httpResponse);
                                return parcel.put("resp", httpResponse)
                                        .put("bg", Thread.currentThread().getName());
                            }
                        })
                        .deliver(new UiWorker() {
                            @Override
                            public Parcel doWork(Parcel parcel) {
                                HttpResponse httpResponse = parcel.get("resp");
                                String name = "bg:" + parcel.get("bg") + ", ui:"
                                        + Thread.currentThread().getName();
                                ToastUtils.showShortToast(
                                        BaseActivity.this,
                                        "resp:" + httpResponse.getCode()
                                                + "(" + name + ")"
                                );
                                return null;
                            }
                        })
                        .end();
                break;
        }
    };
}

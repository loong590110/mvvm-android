package com.mylive.live.base;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mylive.live.exception.ProhibitedException;
import com.mylive.live.arch.mvvm.CommunicableActivity;
import com.mylive.live.arch.observer.Observer;
import com.mylive.live.arch.workflow.BackgroundWorker;
import com.mylive.live.arch.workflow.Parcel;
import com.mylive.live.arch.workflow.UiWorker;
import com.mylive.live.arch.workflow.WorkFlow;
import com.mylive.live.interceptor.HttpInterceptorsManager;
import com.mylive.live.model.HttpResp;
import com.mylive.live.utils.ToastUtils;

/**
 * Created by Developer Zailong Shi on 2019-06-27.
 */
@SuppressLint("Registered")
public class BaseActivity extends CommunicableActivity {

    @Deprecated
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        throw new ProhibitedException("Please start activity by extends ActivityStarter class.");
    }

    @Deprecated
    @Override
    public void startActivity(Intent intent, Bundle options) {
        super.startActivity(intent, options);
        throw new ProhibitedException("Please start activity by extends ActivityStarter class.");
    }

    @Deprecated
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        throw new ProhibitedException("Please start activity by extends ActivityStarter class.");
    }

    @Deprecated
    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        throw new ProhibitedException("Please start activity by extends ActivityStarter class.");
    }

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
                                HttpResp<String> httpResp = JSON.parseObject(
                                        respText,
                                        new TypeReference<HttpResp<String>>() {
                                        }.getType()
                                );
                                publish(httpResp);
                                return parcel.put("resp", httpResp)
                                        .put("bg", Thread.currentThread().getName());
                            }
                        })
                        .deliver(new UiWorker() {
                            @Override
                            public Parcel doWork(Parcel parcel) {
                                HttpResp httpResp = parcel.get("resp");
                                String name = "bg:" + parcel.get("bg") + ", ui:"
                                        + Thread.currentThread().getName();
                                ToastUtils.showShortToast(
                                        BaseActivity.this,
                                        "resp:" + httpResp.getCode()
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

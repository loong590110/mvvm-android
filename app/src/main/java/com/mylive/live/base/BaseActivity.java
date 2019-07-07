package com.mylive.live.base;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mylive.live.arch.mvvm.CommunicableActivity;
import com.mylive.live.arch.observer.Observer;
import com.mylive.live.arch.workflow.BackgroundWorker;
import com.mylive.live.arch.workflow.WorkFlow;
import com.mylive.live.config.HttpStatusCode;
import com.mylive.live.dialog.AlertDialog;
import com.mylive.live.exception.ProhibitedException;
import com.mylive.live.interceptor.HttpInterceptorsManager;
import com.mylive.live.model.HttpResp;
import com.mylive.live.utils.ToastUtils;
import com.mylive.live.widget.NavigationBar;

/**
 * Created by Developer Zailong Shi on 2019-06-27.
 */
@SuppressLint("Registered")
public class BaseActivity extends CommunicableActivity
        implements NavigationBar.OnBackButtonClickListener {

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
        WorkFlow.begin(respText)
                .deliver(BackgroundWorker.work(parcel ->
                        (HttpResp) JSON.parseObject(parcel,
                                new TypeReference<HttpResp<String>>() {
                                }.getType())))
                .end(resp -> {
                    if (resp == null) {
                        return;
                    }
                    switch (getLifecycle().getCurrentState()) {
                        case INITIALIZED:
                        case DESTROYED:
                            break;
                        case CREATED:
                            break;
                        case STARTED:
                            break;
                        case RESUMED:
                            if (resp.getCode() == HttpStatusCode.TOKEN_EXPIRE) {
                                new AlertDialog.Builder(this)
                                        .setTitle("下线通知")
                                        .setMessage("您的账号已经在其他设备上登录，" +
                                                "请确认是否是本人操作！")
                                        .setCancelText("谢谢提醒")
                                        .setConfirmText("重新登录")
                                        .setOnConfirmClickListener((dialog, which) -> {
                                            dialog.dismiss();
                                        })
                                        .show();
                            }
                            break;
                        default:
                            if (!resp.isSuccessful()) {
                                ToastUtils.showShortToast(this, resp.getMessage());
                            }
                    }
                });
    };

    @Override
    public void onBackButtonClick(View v) {
        finish();
    }
}

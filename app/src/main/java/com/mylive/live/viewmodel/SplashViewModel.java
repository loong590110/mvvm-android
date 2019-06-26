package com.mylive.live.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.CountDownTimer;

import com.mylive.live.arch.annotation.Service;
import com.mylive.live.arch.http.ObserverResponse;
import com.mylive.live.arch.http.ObserverSuccess;
import com.mylive.live.arch.mvvm.BaseViewModel;
import com.mylive.live.model.Config;
import com.mylive.live.service.ConfigService;
import com.mylive.live.service.TestService;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public class SplashViewModel extends BaseViewModel {

    private static final long COUNT_DOWN_TIME = 5 * 1000;
    private static final long COUNT_DOWN_INTERVAL = 1000;
    private MutableLiveData<Integer> countDownTimer;
    private MutableLiveData<Config> config;
    private MutableLiveData<String> test;
    @Service
    private ConfigService configService;
    @Service
    private TestService testService;

    public LiveData<Integer> startCountDownTimer() {
        if (countDownTimer == null) {
            countDownTimer = new MutableLiveData<>();
        }
        new CountDownTimer(COUNT_DOWN_TIME, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                countDownTimer.postValue((int) (millisUntilFinished / COUNT_DOWN_INTERVAL) + 1);
            }

            @Override
            public void onFinish() {
                countDownTimer.postValue(0);
            }
        }.start();
        return countDownTimer;
    }

    public LiveData<Config> getConfig() {
        if (config == null) {
            config = new MutableLiveData<>();
        }
        configService.getConfig()
                .observe((ObserverSuccess<Config>) config -> {
                    this.config.postValue(config);
                }, throwable -> {
                    this.config.postValue(null);
                });
        return config;
    }

    public LiveData<String> getTestData() {
        if (test == null) {
            test = new MutableLiveData<>();
        }
        testService.test()
                .observe((ObserverResponse<String>) s -> {
                    test.postValue(s);
                });
        return test;
    }
}

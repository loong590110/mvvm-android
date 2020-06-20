package com.mylive.live.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.os.CountDownTimer;

import com.mylive.live.arch.annotation.Model;
import com.mylive.live.base.BaseViewModel;
import com.mylive.live.model.beans.Config;
import com.mylive.live.model.service.ConfigService;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public class SplashViewModel extends BaseViewModel {

    private static final long COUNT_DOWN_TIME = 3 * 1000;
    private static final long COUNT_DOWN_INTERVAL = 30;
    private MutableLiveData<Integer> countDownTimer;
    private MutableLiveData<Config> config;
    private MutableLiveData<String> test;
    @Model
    private ConfigService configService;

    public LiveData<Integer> startCountDownTimer() {
        if (countDownTimer == null) {
            countDownTimer = new MutableLiveData<>();
        }
        new CountDownTimer(COUNT_DOWN_TIME, COUNT_DOWN_INTERVAL) {
            private long tick = COUNT_DOWN_TIME / COUNT_DOWN_INTERVAL;

            @Override
            public void onTick(long millisUntilFinished) {
                countDownTimer.postValue((int) tick--);
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
        configService.getConfig().observe((Config config) -> {
            this.config.postValue(config);
        }, e -> {
            this.config.postValue(new Config());
        });
        return config;
    }
}

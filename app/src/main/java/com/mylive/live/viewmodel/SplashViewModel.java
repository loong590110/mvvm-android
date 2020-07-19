package com.mylive.live.viewmodel;

import android.os.CountDownTimer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mylive.live.arch.annotation.Service;
import com.mylive.live.arch.http.HttpException;
import com.mylive.live.arch.livedata.MutexLiveData;
import com.mylive.live.base.BaseViewModel;
import com.mylive.live.model.beans.Config;
import com.mylive.live.model.service.ConfigService;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public class SplashViewModel extends BaseViewModel {

    private static final long COUNT_DOWN_TIME = 3 * 1000;
    private static final long COUNT_DOWN_INTERVAL = 30;
    @Service
    private ConfigService configService;

    public LiveData<Integer> startCountDownTimer() {
        MutableLiveData<Integer> countDownTimer = new MutableLiveData<>();
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

    public MutexLiveData<Config, HttpException> getConfig() {
        MutexLiveData<Config, HttpException> configLiveData =
                new MutexLiveData<>();
        configService.getConfig().observe(
                configLiveData::postPositiveValue,
                configLiveData::postNegativeValue
        );
        return configLiveData;
    }
}

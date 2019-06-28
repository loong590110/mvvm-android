package com.mylive.live.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.mylive.live.arch.annotation.Service;
import com.mylive.live.base.BaseViewModel;
import com.mylive.live.model.Config;
import com.mylive.live.model.LiveRoom;
import com.mylive.live.service.ConfigService;

public class LiveRoomViewModel extends BaseViewModel {

    private MutableLiveData<LiveRoom> liveRoomData;
    @Service
    private ConfigService configService;

    public LiveData<Config> getConfig() {
        MutableLiveData<Config> liveData = new MutableLiveData<>();
        configService.getConfig().dispose(this).observe(liveData::postValue);
        return liveData;
    }
}

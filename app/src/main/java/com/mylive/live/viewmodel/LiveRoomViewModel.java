package com.mylive.live.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mylive.live.arch.annotation.Service;
import com.mylive.live.base.BaseViewModel;
import com.mylive.live.model.beans.Config;
import com.mylive.live.model.beans.LiveRoom;
import com.mylive.live.model.service.ConfigService;

public class LiveRoomViewModel extends BaseViewModel {

    private MutableLiveData<LiveRoom> liveRoomData;

    @Service
    private ConfigService configService;

    public LiveData<Config> getConfig() {
        MutableLiveData<Config> liveData = new MutableLiveData<>();
        configService.getConfig().observe(liveData::postValue);
        return liveData;
    }
}

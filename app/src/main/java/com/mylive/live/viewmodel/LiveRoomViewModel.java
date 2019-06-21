package com.mylive.live.viewmodel;

import android.arch.lifecycle.MutableLiveData;

import com.mylive.live.core.base.BaseViewModel;
import com.mylive.live.model.LiveRoom;

public class LiveRoomViewModel extends BaseViewModel {

    private MutableLiveData<LiveRoom> liveRoomData;
}

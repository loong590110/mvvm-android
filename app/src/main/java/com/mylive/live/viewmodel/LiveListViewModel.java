package com.mylive.live.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mylive.live.arch.annotation.Model;
import com.mylive.live.base.BaseViewModel;
import com.mylive.live.model.beans.LiveList;
import com.mylive.live.model.service.LiveListService;

/**
 * Create by zailongshi on 2019/6/22
 */
public class LiveListViewModel extends BaseViewModel {
    @Model
    private LiveListService liveListService;

    public LiveData<LiveList> getLiveList(int pageIndex, int pageSize) {
        MutableLiveData<LiveList> finalLiveList = new MutableLiveData<>();
        liveListService.getLiveList(pageIndex, pageSize)
                .observe(
                        finalLiveList::postValue,
                        e -> finalLiveList.postValue(null)
                );
        return finalLiveList;
    }
}

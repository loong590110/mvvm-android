package com.mylive.live.viewmodel;

import com.mylive.live.arch.annotation.Service;
import com.mylive.live.arch.http.HttpException;
import com.mylive.live.arch.livedata.MutexLiveData;
import com.mylive.live.base.BaseViewModel;
import com.mylive.live.model.beans.LiveList;
import com.mylive.live.model.service.LiveListService;

/**
 * Create by zailongshi on 2019/6/22
 */
public class LiveListViewModel extends BaseViewModel {
    @Service
    private LiveListService liveListService;

    public MutexLiveData<LiveList, HttpException> getLiveList(int pageIndex, int pageSize) {
        MutexLiveData<LiveList, HttpException> finalLiveList = new MutexLiveData<>();
        liveListService.getLiveList(pageIndex, pageSize).observe(
                finalLiveList::postPositiveValue,
                finalLiveList::postNegativeValue
        );
        return finalLiveList;
    }
}

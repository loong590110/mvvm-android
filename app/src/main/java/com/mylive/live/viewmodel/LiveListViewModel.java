package com.mylive.live.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.SparseArray;

import com.mylive.live.arch.annotation.Service;
import com.mylive.live.base.BaseViewModel;
import com.mylive.live.model.LiveList;
import com.mylive.live.service.LiveListService;

/**
 * Create by zailongshi on 2019/6/22
 */
public class LiveListViewModel extends BaseViewModel {

    private MutableLiveData<SparseArray<LiveList>> liveListMap = new MutableLiveData<>();
    @Service
    private LiveListService liveListService;

    public LiveData<LiveList> getLiveList(boolean more) {
        MutableLiveData<LiveList> finalLiveList = new MutableLiveData<>();
        liveListService.getLiveList()
                .dispose(this)
                .observe((LiveList liveList) -> {
                    SparseArray<LiveList> liveListArray = liveListMap.getValue();
                    if (liveListArray == null) {
                        liveListArray = new SparseArray<>();
                    }
                    if (!more) {
                        liveListArray.put(liveList.type, liveList);
                    } else {
                        LiveList _liveList = liveListArray.get(liveList.type);
                        liveList.list.removeAll(_liveList.list);
                        _liveList.list.addAll(liveList.list);
                        liveListArray.put(_liveList.type, _liveList);
                    }
                    liveListMap.setValue(liveListArray);
                    finalLiveList.postValue(liveList);
                });
        return finalLiveList;
    }
}

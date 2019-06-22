package com.mylive.live.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.SparseArray;

import com.mylive.live.core.base.BaseViewModel;
import com.mylive.live.core.http.ObserverSuccess;
import com.mylive.live.core.http.Retrofit2;
import com.mylive.live.core.workflow.BackgroundWorker;
import com.mylive.live.core.workflow.Parcel;
import com.mylive.live.core.workflow.IoWorker;
import com.mylive.live.core.workflow.UiWorker;
import com.mylive.live.core.workflow.WorkFlow;
import com.mylive.live.model.LiveList;
import com.mylive.live.service.LiveListService;

/**
 * Create by zailongshi on 2019/6/22
 */
public class LiveListViewModel extends BaseViewModel {

    private MutableLiveData<SparseArray<LiveList>> liveListMap = new MutableLiveData<>();

    public LiveData<LiveList> getLiveList(boolean more) {
        MutableLiveData<LiveList> finalLiveList = new MutableLiveData<>();
        Retrofit2.getDefault()
                .create(LiveListService.class)
                .getLiveList()
                .dispose(this)
                .observe(new ObserverSuccess<LiveList>() {
                    @Override
                    public void onChanged(LiveList liveList) {
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
                    }
                });
        return finalLiveList;
    }

    public LiveData<String> testWorkFlow() {
        MutableLiveData<String> workResult = new MutableLiveData<>();
        WorkFlow.begin()
                .deliver(new IoWorker() {
                    @Override
                    public Parcel doWork(Parcel parcel) {
                        parcel.put("name", "Aaron")
                                .put("age", 18);
                        return parcel;
                    }
                })
                .deliver(new BackgroundWorker() {
                    @Override
                    public Parcel doWork(Parcel parcel) {
                        return parcel.remove("age");
                    }
                })
                .deliver(new UiWorker() {
                    @Override
                    public Parcel doWork(Parcel parcel) {
                        workResult.setValue(parcel.toString());
                        return parcel;
                    }
                })
                .end();
        return workResult;
    }
}

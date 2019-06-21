package com.mylive.live.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.SparseArray;

import com.mylive.live.core.base.BaseViewModel;
import com.mylive.live.core.http.ObserverSuccess;
import com.mylive.live.core.http.Retrofit2;
import com.mylive.live.core.workflow.BackgroundWork;
import com.mylive.live.core.workflow.Data;
import com.mylive.live.core.workflow.IoWork;
import com.mylive.live.core.workflow.UiWork;
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
                .addWork(new IoWork() {
                    @Override
                    public Data doWork(Data data) {
                        data.put("name", "Aaron")
                                .put("age", 18);
                        return data;
                    }
                })
                .addWork(new BackgroundWork() {
                    @Override
                    public Data doWork(Data data) {
                        return data.remove("age");
                    }
                })
                .addWork(new UiWork() {
                    @Override
                    public Data doWork(Data data) {
                        workResult.setValue(data.toString());
                        return data;
                    }
                })
                .end();
        return workResult;
    }
}

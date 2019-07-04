package com.mylive.live.service;

import com.mylive.live.arch.http.Observable;
import com.mylive.live.model.HttpResp;
import com.mylive.live.model.LiveList;

public interface LiveListService {

    Observable<HttpResp<LiveList>> getLiveList();
}

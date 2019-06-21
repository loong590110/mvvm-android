package com.mylive.live.service;

import com.mylive.live.core.http.Observable;
import com.mylive.live.model.HttpResponse;
import com.mylive.live.model.LiveList;

public interface LiveListService {

    Observable<HttpResponse<LiveList>> getLiveList();
}

package com.mylive.live.service;

import com.mylive.live.arch.http.Observable;
import com.mylive.live.model.Config;
import com.mylive.live.model.HttpResponse;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public interface ConfigService {
    @GET("/pages/config?id=100309")
    Observable<HttpResponse<Config>> getConfig();
}

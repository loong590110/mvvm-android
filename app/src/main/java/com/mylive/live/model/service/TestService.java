package com.mylive.live.model.service;

import com.mylive.live.arch.http.Observable;

import retrofit2.http.GET;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public interface TestService {

    @GET("https://im.qq.com/")
    Observable<String> test();
}

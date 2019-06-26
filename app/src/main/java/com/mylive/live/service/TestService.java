package com.mylive.live.service;

import com.mylive.live.arch.http.Observable;

import retrofit2.http.GET;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public interface TestService {

    @GET("https://www.baidu.com/")
    Observable<String> test();
}

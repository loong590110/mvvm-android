package com.mylive.live.service;

import com.mylive.live.core.http.Observable;

import retrofit2.http.GET;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public interface TestService {

    @GET("/")
    Observable<String> test();
}

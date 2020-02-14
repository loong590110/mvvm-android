package com.mylive.live.service;

import com.mylive.live.arch.http.Observable;
import com.mylive.live.model.HttpResp;
import com.mylive.live.model.LiveList;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LiveListService {

    /**
     * GET: {host}/api/livelist.js?page_index={page_index}&page_size={page_size}
     *
     * @param pageIndex 页码
     * @param pageSize  每页条数
     * @return data list
     */
    @GET("/pages/api/livelist.js")
    Observable<HttpResp<LiveList>> getLiveList(
            @Query("page_index") int pageIndex, @Query("page_size") int pageSize
    );
}

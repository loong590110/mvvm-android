package com.mylive.live.service;

import com.mylive.live.arch.http.Observable;
import com.mylive.live.model.HttpResp;
import com.mylive.live.model.LiveList;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LiveListService {

    /**
     * @script
     * //GET: {host}/api/livelist.js?desc_head={desc_head}&size={size}
     * var list = [];
     * for (let i = 0; i < size; i++) {
     *     list.push({"desc":desc_head + '_item' + i, "id": i + 1});
     * };
     * var json = {
     *     "code":200,
     *     "message":"ok",
     *     "data": {
     *         "type":100,
     *         "list": list
     *     }
     * };
     * JSON.stringify(json);
     * @param size
     * @return
     */
    @GET("http://172.16.14.151:8080/api/livelist.js?desc_head=live")
    Observable<HttpResp<LiveList>> getLiveList(@Query("size") int size);
}

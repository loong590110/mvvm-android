package com.mylive.live.model.beans;

import com.mylive.live.arch.http.HttpContent;
import com.mylive.live.arch.http.HttpStatusCode;
import com.mylive.live.arch.http.HttpStatusDesc;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public class HttpResp<T> {

    private static final int STATUS_OK = 200;

    @HttpStatusCode(STATUS_OK = STATUS_OK)
    private int code;
    @HttpStatusDesc
    private String message;
    @HttpContent
    private T data;

    public boolean isSuccessful() {
        return code == STATUS_OK;
    }

    public T getData() {
        return data;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

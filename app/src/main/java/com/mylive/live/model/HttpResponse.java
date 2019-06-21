package com.mylive.live.model;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public class HttpResponse<T> implements com.mylive.live.core.http.HttpResponse {

    private int code;
    private String status;
    private T data;

    @Override
    public boolean isSuccessful() {
        return code == 200;
    }

    @Override
    public T getData() {
        return data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setData(T data) {
        this.data = data;
    }
}

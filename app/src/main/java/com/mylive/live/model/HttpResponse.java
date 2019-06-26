package com.mylive.live.model;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public class HttpResponse<T> implements com.mylive.live.arch.http.HttpResponse {

    private int code;
    private String message;
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

    @Override
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

package com.mylive.live.arch.exception;

/**
 * Create by zailongshi on 2019/6/25
 */
public class HttpException extends Exception {

    private int code;

    public HttpException() {
        super();
    }

    public HttpException(String message) {
        super(message);
    }

    public HttpException(Throwable throwable) {
        super(throwable);
    }

    public HttpException(int code, String message) {
        super(message);
        this.code = code;
    }

    public HttpException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public HttpException(int code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

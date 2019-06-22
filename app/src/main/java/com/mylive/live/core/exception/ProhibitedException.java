package com.mylive.live.core.exception;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public class ProhibitedException extends RuntimeException {

    public ProhibitedException() {
        super();
    }

    public ProhibitedException(String message) {
        super(message);
    }
}

package com.mylive.live.core.workflow;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by zailongshi on 2019/6/22
 */
public class Parcel {

    private Map<String, Object> caches = new HashMap<>();

    public <T> Parcel put(String key, T value) {
        caches.put(key, value);
        return this;
    }

    public <T> T get(String key) {
        //noinspection unchecked
        return (T) caches.get(key);
    }

    public Parcel remove(String key) {
        caches.remove(key);
        return this;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return caches.toString();
    }
}

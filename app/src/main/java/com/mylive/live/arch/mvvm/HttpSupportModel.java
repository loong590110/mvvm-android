package com.mylive.live.arch.mvvm;

import com.mylive.live.arch.annotation.Model;
import com.mylive.live.arch.http.ServiceCreator;

import java.lang.reflect.Field;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public class HttpSupportModel {
    {
        for (Field field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Model.class)) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                Object service = null;
                try {
                    service = field.get(this);
                } catch (IllegalAccessException ignore) {
                }
                if (service == null) {
                    Class<?> serviceType = field.getType();
                    service = ServiceCreator.create(serviceType);
                    try {
                        field.set(this, service);
                    } catch (IllegalAccessException ignore) {
                    }
                }
            }
        }
    }
}

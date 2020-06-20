package com.mylive.live.arch.mvvm;

import androidx.lifecycle.ViewModel;

import com.mylive.live.arch.annotation.Model;

import java.lang.reflect.Field;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public class HttpSupportViewModel extends ViewModel {
    {
        for (Field field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Model.class)) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                Object model = null;
                try {
                    model = field.get(this);
                } catch (IllegalAccessException ignore) {
                }
                if (model == null) {
                    Class<?> modelType = field.getType();
                    try {
                        model = modelType.newInstance();
                    } catch (IllegalAccessException ignore) {
                    } catch (InstantiationException ignore) {
                    }
                    try {
                        field.set(this, model);
                    } catch (IllegalAccessException ignore) {
                    }
                }
            }
        }
    }
}

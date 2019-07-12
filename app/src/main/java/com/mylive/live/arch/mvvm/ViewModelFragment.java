package com.mylive.live.arch.mvvm;

import android.arch.lifecycle.ViewModelProviders;

import com.mylive.live.arch.annotation.ViewModel;

import java.lang.reflect.Field;

/**
 * Created by Developer Zailong Shi on 2019-07-11.
 */
public class ViewModelFragment extends CommunicableFragment {
    {
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ViewModel.class)) {
                if (android.arch.lifecycle.ViewModel.class.isAssignableFrom(field.getType())) {
                    Class<? extends android.arch.lifecycle.ViewModel> viewModelType
                            = (Class<? extends android.arch.lifecycle.ViewModel>) field.getType();
                    try {
                        if (!field.isAccessible()) {
                            field.setAccessible(true);
                        }
                        field.set(this, ViewModelProviders.of(getActivity())
                                .get(viewModelType));
                    } catch (IllegalAccessException ignore) {
                    }
                }
            }
        }
    }
}
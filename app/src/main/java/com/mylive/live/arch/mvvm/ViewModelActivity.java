package com.mylive.live.arch.mvvm;

import android.annotation.SuppressLint;
import androidx.lifecycle.ViewModelProviders;

import com.mylive.live.arch.annotation.ViewModel;

import java.lang.reflect.Field;

/**
 * Created by Developer Zailong Shi on 2019-07-11.
 */
@SuppressLint("Registered")
public class ViewModelActivity extends CommunicableActivity {
    {
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ViewModel.class)) {
                if (androidx.lifecycle.ViewModel.class.isAssignableFrom(field.getType())) {
                    Class<? extends androidx.lifecycle.ViewModel> viewModelType
                            = (Class<? extends androidx.lifecycle.ViewModel>) field.getType();
                    try {
                        if (!field.isAccessible()) {
                            field.setAccessible(true);
                        }
                        field.set(this, ViewModelProviders.of(this)
                                .get(viewModelType));
                    } catch (IllegalAccessException ignore) {
                    }
                }
            }
        }
    }
}

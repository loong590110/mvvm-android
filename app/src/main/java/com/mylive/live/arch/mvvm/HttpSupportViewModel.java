package com.mylive.live.arch.mvvm;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.mylive.live.arch.annotation.Service;
import com.mylive.live.arch.http.ServiceCreator;

import java.lang.reflect.Field;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public class HttpSupportViewModel extends ViewModel implements LifecycleOwner, LifecycleObserver {

    private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    {
        lifecycleRegistry.markState(Lifecycle.State.CREATED);
        lifecycleRegistry.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void initializeServices() {
        for (Field field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Service.class)) {
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

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    @Override
    protected void onCleared() {
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED);
    }
}

package com.mylive.live.arch.mvvm;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.mylive.live.arch.annotation.Service;
import com.mylive.live.arch.http.ServiceCreator;

import java.lang.reflect.Field;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public class HttpSupportViewModel extends ViewModel implements LifecycleOwner {

    {
        for (Field field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Service.class)) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                Object service = null;
                try {
                    service = field.get(this);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (service == null) {
                    Class<?> serviceType = field.getType();
                    service = ServiceCreator.create(serviceType);
                    try {
                        field.set(this, service);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    private Lifecycle lifecycle = new Lifecycle() {
        @Override
        public void addObserver(@NonNull LifecycleObserver observer) {
            lifecycleRegistry.addObserver(observer);
        }

        @Override
        public void removeObserver(@NonNull LifecycleObserver observer) {
            lifecycleRegistry.removeObserver(observer);
        }

        @NonNull
        @Override
        public State getCurrentState() {
            return lifecycleRegistry.getCurrentState();
        }
    };

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycle;
    }

    @Override
    protected void onCleared() {
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED);
    }
}

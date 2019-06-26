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
public class BaseViewModel extends ViewModel implements LifecycleOwner {

    {
        for (Field field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Service.class)) {
                Class<?> service = field.getType();
                Object t = ServiceCreator.create(service);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                try {
                    field.set(this, t);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
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

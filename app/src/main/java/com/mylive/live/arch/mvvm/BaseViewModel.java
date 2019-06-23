package com.mylive.live.arch.mvvm;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public class BaseViewModel extends ViewModel implements LifecycleOwner {

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
    protected final void onCleared() {
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED);
    }
}

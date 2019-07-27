package com.mylive.live.arch.workflow;

import androidx.lifecycle.LifecycleOwner;

/**
 * Create by zailongshi on 2019/6/22
 */
public final class WorkFlow {

    public static <T> WorkManager<T> begin() {
        return new WorkManagerImpl<>();
    }

    public static <T> WorkManager<T> begin(LifecycleOwner owner) {
        return new WorkManagerImpl<>(owner);
    }

    public static <T> WorkManager<T> begin(T parcel) {
        return new WorkManagerImpl<>(parcel);
    }

    public static <T> WorkManager<T> begin(LifecycleOwner owner, T parcel) {
        return new WorkManagerImpl<>(owner, parcel);
    }
}

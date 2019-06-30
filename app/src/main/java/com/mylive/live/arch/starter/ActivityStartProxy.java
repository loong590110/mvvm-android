package com.mylive.live.arch.starter;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created By zailongshi on 2018/12/20.
 */
final class ActivityStartProxy {

    private static HashMap<String, WeakReference<LifecycleOwner>> contexts;

    private static boolean prevent(LifecycleOwner context) {
        if (ifPrevent(context)) {
            return true;
        }
        if (contexts == null) {
            contexts = new HashMap<>();
        }
        final String key = String.valueOf(context);
        context.getLifecycle().addObserver(new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            public void onResume() {
                remove();
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            public void onDestroy() {
                remove();
            }

            private void remove() {
                if (contexts == null)
                    return;
                WeakReference<LifecycleOwner> reference = contexts.get(key);
                if (reference != null) {
                    LifecycleOwner ctx = reference.get();
                    if (ctx != null) {
                        ctx.getLifecycle().removeObserver(this);
                    }
                }
                contexts.remove(key);
            }
        });
        contexts.put(key, new WeakReference<>(context));
        return false;
    }

    static boolean ifPrevent(LifecycleOwner context) {
        if (context == null) {
            return true;
        }
        if (contexts == null) {
            return false;
        }
        final String key = String.valueOf(context);
        WeakReference<LifecycleOwner> reference = contexts.get(key);
        return reference != null && reference.get() != null;
    }

    static void startActivity(FragmentActivity context, Intent intent) {
        if (prevent(context)) {
            return;
        }
        try {
            ActivityCompat.startActivity(context, intent, null);
        } catch (Exception ignore) {
        }
    }

    static void startActivity(Fragment fragment, Intent intent) {
        if (prevent(fragment)) {
            return;
        }
        try {
            fragment.startActivity(intent);
        } catch (Exception ignore) {
        }
    }

    static void startActivityForResult(FragmentActivity context, Intent intent,
                                       int requestCode) {
        if (prevent(context)) {
            return;
        }
        try {
            ActivityCompat.startActivityForResult(context, intent,
                    requestCode, null);
        } catch (Exception ignore) {
        }
    }

    static void startActivityForResult(Fragment fragment, Intent intent,
                                       int requestCode) {
        if (prevent(fragment)) {
            return;
        }
        try {
            fragment.startActivityForResult(intent, requestCode);
        } catch (Exception ignore) {
        }
    }
}

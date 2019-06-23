package com.mylive.live.arch.starter;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created By zailongshi on 2018/12/20.
 */
final class ActivityStartProxy {

    private static HashMap<String, WeakReference<FragmentActivity>> contexts;

    private static boolean prevent(FragmentActivity context) {
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
                WeakReference<FragmentActivity> reference = contexts.get(key);
                if (reference != null) {
                    FragmentActivity ctx = reference.get();
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

    public static boolean ifPrevent(FragmentActivity context) {
        if (context == null) {
            return true;
        }
        if (contexts == null) {
            return false;
        }
        final String key = String.valueOf(context);
        WeakReference<FragmentActivity> reference = contexts.get(key);
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
}

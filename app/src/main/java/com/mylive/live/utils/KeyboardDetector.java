package com.mylive.live.utils;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import java.util.Objects;

/**
 * Created by Developer Zailong Shi on 2019-08-01.
 */
public final class KeyboardDetector {

    public static void start(FragmentActivity activity, OnStateChangedListener l) {
        new KeyboardDetector(activity, l);
    }

    private OnStateChangedListener onStateChangedListener;

    private KeyboardDetector(FragmentActivity activity, OnStateChangedListener l) {
        Objects.requireNonNull(activity);
        Objects.requireNonNull(l);
        this.onStateChangedListener = l;
        PopupWindow popupWindow = new PopupWindow(0, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setOutsideTouchable(false);
        int screenHeight = activity.getResources().getDisplayMetrics().heightPixels;
        View view = new View(activity) {
            private int deltaHeight;

            @Override
            protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                super.onSizeChanged(w, h, oldw, oldh);
                int diff = h - oldh;
                if (Math.abs(diff) > screenHeight / 4) {
                    if (diff < 0) {
                        if (deltaHeight != diff) {
                            deltaHeight = diff;
                            notifyKeyboardStateChanged();
                        }
                    } else {
                        if (deltaHeight != 0) {
                            deltaHeight = 0;
                            notifyKeyboardStateChanged();
                        }
                    }
                }
            }

            private void notifyKeyboardStateChanged() {
                if (onStateChangedListener != null) {
                    onStateChangedListener.onStateChanged(deltaHeight);
                }
            }
        };
        popupWindow.setContentView(view);
        activity.getLifecycle().addObserver(new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            void onDestroy() {
                popupWindow.dismiss();
            }
        });
        View parent = activity.getWindow().getDecorView();
        parent.post(new Runnable() {
            @Override
            public void run() {
                if (parent.getWindowToken() != null) {
                    popupWindow.showAtLocation(parent, 0, 0, 0);
                } else {
                    parent.postDelayed(this, 50);
                }
            }
        });
    }

    public interface OnStateChangedListener {
        void onStateChanged(int deltaHeight);
    }
}

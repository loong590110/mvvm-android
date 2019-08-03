package com.mylive.live.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.widget.PopupWindow;

import java.util.Objects;

/**
 * Created by Developer Zailong Shi on 2019-08-01.
 */
public final class KeyboardDetector {

    public static void start(Activity activity, OnStateChangedListener l) {
        new KeyboardDetector(activity, l);
    }

    private View view;
    private int resizeOffset;
    private OnStateChangedListener onStateChangedListener;

    private KeyboardDetector(Activity activity, OnStateChangedListener l) {
        Objects.requireNonNull(activity);
        Objects.requireNonNull(l);
        this.onStateChangedListener = l;
        view = new View(activity);
        view.setBackgroundColor(0xffff0000);
        view.getViewTreeObserver()
                .addOnGlobalLayoutListener(this::possiblyResizeChildOfContent);
        view.post(() -> {
            PopupWindow window = new PopupWindow(100,
                    100);
            window.setContentView(view);
            window.showAtLocation(view, 0, 0, 0);
        });
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        int currentHeight = view.getHeight();
        if (currentHeight <= 0) {
            return;
        }
        if (usableHeightNow < currentHeight) {
            // keyboard probably just became visible
            int diff = currentHeight - usableHeightNow;
            if (resizeOffset != diff) {
                resizeOffset = diff;
                notifyKeyboardHeightChanged();
            }
        } else {
            // keyboard probably just became hidden
            if (resizeOffset != 0) {
                resizeOffset = 0;
                notifyKeyboardHeightChanged();
            }
        }
    }

    private void notifyKeyboardHeightChanged() {
        onStateChangedListener.onStateChanged(resizeOffset);
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        view.getWindowVisibleDisplayFrame(r);
        return r.bottom;
    }

    public interface OnStateChangedListener {
        void onStateChanged(int height);
    }
}

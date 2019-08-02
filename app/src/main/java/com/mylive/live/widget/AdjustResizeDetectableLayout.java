package com.mylive.live.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Developer Zailong Shi on 2018/10/17.
 */
public class AdjustResizeDetectableLayout extends FrameLayout {

    private int deltaHeight;
    private OnStateChangedListener onStateChangedListener;

    public AdjustResizeDetectableLayout(@NonNull Context context) {
        this(context, null);
    }

    public AdjustResizeDetectableLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdjustResizeDetectableLayout(@NonNull Context context, @Nullable AttributeSet attrs,
                                        int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != VISIBLE) {
            onKeyboardHidden();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getWidth() > 0 && getHeight() > 0) {
            possiblyAdjustResize();
            super.onMeasure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        if (isKeyboardVisible()) {
            return insets;
        }
        return super.dispatchApplyWindowInsets(insets);
    }

    private void possiblyAdjustResize() {
        int currentHeight = getHeight();
        if (currentHeight <= 0) {
            return;
        }
        int usableHeightNow = computeUsableHeight();
        int diff = currentHeight - usableHeightNow;
        if (diff > 0) {
            // keyboard probably just became visible
            if (deltaHeight != diff) {
                deltaHeight = diff;
                notifyKeyboardStateChanged();
            }
        } else {
            // keyboard probably just became hidden
            onKeyboardHidden();
        }
    }

    private void onKeyboardHidden() {
        if (deltaHeight != 0) {
            deltaHeight = 0;
            notifyKeyboardStateChanged();
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        getWindowVisibleDisplayFrame(r);
        return r.bottom - (isImmersiveMode() ? 0 : r.top);
    }

    private void notifyKeyboardStateChanged() {
        if (onStateChangedListener != null) {
            onStateChangedListener.onStateChanged(deltaHeight);
        }
    }

    private boolean isImmersiveMode() {
        if (getContext() instanceof Activity) {
            int uiVisibility = ((Activity) getContext())
                    .getWindow().getDecorView().getSystemUiVisibility();
            return (uiVisibility & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                    == View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        }
        return false;
    }

    public void setOnStateChangedListener(OnStateChangedListener onStateChangedListener) {
        this.onStateChangedListener = onStateChangedListener;
    }

    public boolean isKeyboardVisible() {
        return deltaHeight > 0;
    }

    public interface OnStateChangedListener {
        void onStateChanged(int deltaHeight);
    }
}

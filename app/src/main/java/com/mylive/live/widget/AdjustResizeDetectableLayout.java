package com.mylive.live.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;

/**
 * Created by Developer Zailong Shi on 2018/10/17.
 */
public class AdjustResizeDetectableLayout extends FrameLayout {

    private int deltaHeight, screenHeight;
    private OnStateChangedListener onStateChangedListener;

    public AdjustResizeDetectableLayout(Context context) {
        this(context, null);
    }

    public AdjustResizeDetectableLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdjustResizeDetectableLayout(Context context, AttributeSet attrs,
                                        int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != VISIBLE) {
            onKeyboardHidden();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec(heightMeasureSpec));
    }

    @Override
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        if (isKeyboardVisible()) {
            return insets;
        }
        return super.dispatchApplyWindowInsets(insets);
    }

    private int heightMeasureSpec(int heightMeasureSpec) {
        int height = getHeight();
        if (height <= 0) {
            return heightMeasureSpec;
        }
        int usableHeightNow = MeasureSpec.getSize(heightMeasureSpec);
        int margin = getMarginTopAndBottom();
        int diff = usableHeightNow - height - margin;
        if (-diff > getScreenHeight() / 4) {
            // keyboard probably just became visible
            if (deltaHeight != diff) {
                deltaHeight = diff;
                notifyKeyboardStateChanged();
            }
        } else if (diff == 0) {
            // keyboard probably just became hidden
            onKeyboardHidden();
        } else {
            height = usableHeightNow - margin;
        }
        return makeMeasureSpec(height);
    }

    private int makeMeasureSpec(int height) {
        return MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
    }

    private int getMarginTopAndBottom() {
        if (getLayoutParams() instanceof MarginLayoutParams) {
            MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
            return params.topMargin + params.bottomMargin;
        }
        return 0;
    }

    private int getScreenHeight() {
        if (screenHeight <= 0) {
            screenHeight = getResources().getDisplayMetrics().heightPixels;
        }
        return screenHeight;
    }

    private void onKeyboardHidden() {
        if (deltaHeight != 0) {
            deltaHeight = 0;
            notifyKeyboardStateChanged();
        }
    }

    private void notifyKeyboardStateChanged() {
        if (onStateChangedListener != null) {
            onStateChangedListener.onStateChanged(deltaHeight);
        }
    }

    public void setOnStateChangedListener(OnStateChangedListener onStateChangedListener) {
        this.onStateChangedListener = onStateChangedListener;
    }

    public boolean isKeyboardVisible() {
        return deltaHeight < 0;
    }

    public interface OnStateChangedListener {
        void onStateChanged(int deltaHeight);
    }
}

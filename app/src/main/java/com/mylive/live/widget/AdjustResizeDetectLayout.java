package com.mylive.live.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Developer Zailong Shi on 2018/10/17.
 */
public class AdjustResizeDetectLayout extends FrameLayout {

    private EditText focusedEditText, lastFocusedEditText;
    private OnChangedListener onChangedListener;
    private AdjustResizeDetector resizeDetector;

    public AdjustResizeDetectLayout(@NonNull Context context) {
        this(context, null);
    }

    public AdjustResizeDetectLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdjustResizeDetectLayout(@NonNull Context context, @Nullable AttributeSet attrs,
                                    int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resizeDetector = AdjustResizeDetector.assist(this);
        resizeDetector.setOnChangedListener(this::notifyKeyboardStateChanged);
    }

    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        if (hasFocusedEditText()) {
            return insets;
        }
        return super.dispatchApplyWindowInsets(insets);
    }

    private void notifyKeyboardStateChanged(int height) {
        if (onChangedListener != null) {
            onChangedListener.onChanged(lastFocusedEditText, height);
        }
    }

    private OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v instanceof EditText) {
                if (focusedEditText == null) {
                    focusedEditText = (EditText) v;
                    lastFocusedEditText = focusedEditText;
                }
            }
            if ((event.getAction() & MotionEvent.ACTION_MASK)
                    == MotionEvent.ACTION_UP) {
                v.performClick();
            }
            return false;
        }
    };

    public void addEditTexts(EditText... views) {
        if (views != null && views.length > 0) {
            for (int i = 0; i < views.length; i++) {
                if (views[i] == null) {
                    throw new IllegalArgumentException("Element index " + i + "in views is null");
                }
                views[i].setOnTouchListener(onTouchListener);
            }
        }
    }

    public void addEditTexts(@IdRes int... viewIds) {
        if (viewIds != null && viewIds.length > 0) {
            EditText[] views = new EditText[viewIds.length];
            for (int i = 0; i < viewIds.length; i++) {
                views[i] = findViewById(viewIds[i]);
                if (views[i] == null) {
                    throw new IllegalArgumentException("Not found the view by id " + viewIds[i]);
                }
            }
            addEditTexts(views);
        }
    }

    private boolean hasFocusedEditText() {
        return focusedEditText != null;
    }

    private void disposeFocusedEditText() {
        focusedEditText = null;
    }

    public void setOnChangedListener(OnChangedListener onChangedListener) {
        this.onChangedListener = onChangedListener;
    }

    public boolean isKeyboardVisible() {
        return resizeDetector.getResizeOffset() > 0;
    }

    public interface OnChangedListener {
        void onChanged(EditText focusedEditText, int height);
    }

    private static class AdjustResizeDetector {

        // For more information, see https://code.google.com/p/android/issues/detail?id=5497
        // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

        private static AdjustResizeDetector assist(AdjustResizeDetectLayout childOfContent) {
            return new AdjustResizeDetector(childOfContent);
        }

        private AdjustResizeDetectLayout mChildOfContent;
        private OnChangedListener onChangedListener;
        private int resizeOffset;

        private AdjustResizeDetector(AdjustResizeDetectLayout childOfContent) {
            mChildOfContent = childOfContent;
            mChildOfContent.getViewTreeObserver()
                    .addOnGlobalLayoutListener(
                            this::possiblyResizeChildOfContent);
        }

        private void possiblyResizeChildOfContent() {
            if (hasFocusedEditText()) {
                int usableHeightNow = computeUsableHeight();
                int currentHeight = mChildOfContent.getHeight();
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
                        disposeFocusedEditText();
                    }
                }
                ViewGroup.LayoutParams params = mChildOfContent.getLayoutParams();
                if (params.height != ViewGroup.LayoutParams.MATCH_PARENT) {
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    mChildOfContent.setLayoutParams(params);
                }
            }
        }

        private void notifyKeyboardHeightChanged() {
            if (onChangedListener != null) {
                onChangedListener.onChanged(resizeOffset);
            }
        }

        private int computeUsableHeight() {
            Rect r = new Rect();
            mChildOfContent.getWindowVisibleDisplayFrame(r);
            return r.bottom;
        }

        private boolean hasFocusedEditText() {
            return mChildOfContent.hasFocusedEditText();
        }

        private void disposeFocusedEditText() {
            mChildOfContent.disposeFocusedEditText();
        }

        private int getResizeOffset() {
            return resizeOffset;
        }

        private void setOnChangedListener(OnChangedListener onChangedListener) {
            this.onChangedListener = onChangedListener;
        }

        private interface OnChangedListener {
            void onChanged(int height);
        }
    }
}

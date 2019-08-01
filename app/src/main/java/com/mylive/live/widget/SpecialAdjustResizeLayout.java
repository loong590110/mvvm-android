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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Developer Zailong Shi on 2018/10/17.
 */
public class SpecialAdjustResizeLayout extends FrameLayout {

    private EditText focusedEditText;
    private View focusedAdjustableView;
    private Map<EditText, View> adjustableViews;
    private OnChangedListener onChangedListener;
    private AdjustResizeDetector resizeDetector;

    public SpecialAdjustResizeLayout(@NonNull Context context) {
        this(context, null);
    }

    public SpecialAdjustResizeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpecialAdjustResizeLayout(@NonNull Context context, @Nullable AttributeSet attrs,
                                     int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resizeDetector = AdjustResizeDetector.assist(this);
        resizeDetector.setOnChangedListener(height -> {
            layoutAdjustableView();
            notifyKeyboardStateChanged(height);
        });
    }

    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        if (hasFocusedEditText()) {
            return insets;
        }
        return super.dispatchApplyWindowInsets(insets);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (hasFocusedEditText()) {
            return;
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    private void layoutAdjustableView() {
        if (!hasFocusedEditText() || !hasAdjustableViews()) {
            return;
        }
        View adjustableView = adjustableViews.get(focusedEditText);
        focusedAdjustableView = adjustableView;
        if (adjustableView != null) {
            adjustableView.layout(adjustableView.getLeft(),
                    adjustableView.getTop() - resizeDetector.getResizeOffset(),
                    adjustableView.getRight(),
                    adjustableView.getBottom() - resizeDetector.getResizeOffset());
        }
    }

    private void notifyKeyboardStateChanged(int height) {
        if (onChangedListener != null) {
            onChangedListener.onChanged(focusedAdjustableView, height);
        }
    }

    private OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v instanceof EditText) {
                if (focusedEditText == null) {
                    focusedEditText = (EditText) v;
                }
            }
            if ((event.getAction() & MotionEvent.ACTION_MASK)
                    == MotionEvent.ACTION_UP) {
                v.performClick();
            }
            return false;
        }
    };

    public void addAdjustableViews(View... views) {
        if (views != null && views.length > 0) {
            if (adjustableViews == null) {
                adjustableViews = new HashMap<>();
            }
            for (int i = 0; i < views.length; i++) {
                if (views[i] == null) {
                    throw new IllegalArgumentException("Element index " + i + "in views is null");
                }
                findEditTextView(views[i], views[i]);
            }
        }
    }

    public void addAdjustableViews(@IdRes int... viewIds) {
        if (viewIds != null && viewIds.length > 0) {
            View[] views = new View[viewIds.length];
            for (int i = 0; i < viewIds.length; i++) {
                views[i] = findViewById(viewIds[i]);
                if (views[i] == null) {
                    throw new IllegalArgumentException("Not found the view by id " + viewIds[i]);
                }
            }
            addAdjustableViews(views);
        }
    }

    private void findEditTextView(View rootNode, View currentNode) {
        if (currentNode instanceof EditText) {
            if (!adjustableViews.containsKey(currentNode)) {
                adjustableViews.put((EditText) currentNode, rootNode);
                currentNode.setOnTouchListener(onTouchListener);
            }
        } else if (currentNode instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) currentNode).getChildCount(); i++) {
                View child = ((ViewGroup) currentNode).getChildAt(i);
                findEditTextView(rootNode, child);
            }
        }
    }

    private boolean hasAdjustableViews() {
        return adjustableViews != null
                && adjustableViews.size() > 0;
    }

    private boolean hasFocusedEditText() {
        return focusedEditText != null;
    }

    private void disposeFocusedEditText(Object disposer) {
        if (disposer instanceof SpecialAdjustResizeLayout) {
            focusedEditText = null;
        } else if (disposer instanceof AdjustResizeDetector) {
            focusedEditText = null;
        }
    }

    public void setOnChangedListener(OnChangedListener onChangedListener) {
        this.onChangedListener = onChangedListener;
    }

    public boolean isKeyboardVisible() {
        return resizeDetector.getResizeOffset() > 0;
    }

    public interface OnChangedListener {
        void onChanged(View focusedView, int height);
    }

    private static class AdjustResizeDetector {

        // For more information, see https://code.google.com/p/android/issues/detail?id=5497
        // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

        private static AdjustResizeDetector assist(SpecialAdjustResizeLayout childOfContent) {
            return new AdjustResizeDetector(childOfContent);
        }

        private SpecialAdjustResizeLayout mChildOfContent;
        private OnChangedListener onChangedListener;
        private int resizeOffset;

        private AdjustResizeDetector(SpecialAdjustResizeLayout childOfContent) {
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
                    resizeOffset = currentHeight - usableHeightNow;
                    notifyKeyboardHeightChanged();
                } else {
                    // keyboard probably just became hidden
                    resizeOffset = 0;
                    notifyKeyboardHeightChanged();
                    disposeFocusedEditText(this);
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

        private void disposeFocusedEditText(Object disposer) {
            mChildOfContent.disposeFocusedEditText(disposer);
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

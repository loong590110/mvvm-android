package com.mylive.live.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

    private boolean resize, autoResize = true;
    private int offsetUp;
    private EditText focusedEditText;
    private Map<EditText, View> adjustableViews;
    private OnChangedListener onChangedListener;

    public SpecialAdjustResizeLayout(@NonNull Context context) {
        this(context, null);
    }

    public SpecialAdjustResizeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpecialAdjustResizeLayout(@NonNull Context context, @Nullable AttributeSet attrs,
                                     int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        AndroidBug5497Workaround.assistActivity(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (resize && h > 0 && oldh > h) {
            offsetUp = oldh - h;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (resize) {
            if (offsetUp > 0) {
                if (autoResize) {
                    layoutChild();
                }
                offsetUp = 0;
                notifyKeyboardStateChanged(offsetUp);
            }
            resize = false;
            return;
        }
        if (autoResize || focusedEditText == null) {
            super.onLayout(changed, left, top, right, bottom);
        }
        notifyKeyboardStateChanged(0);
        //置空输入控件放在最后
        if (focusedEditText != null) {
            focusedEditText = null;
        }
    }

    private void layoutChild() {
        if (focusedEditText == null || adjustableViews == null || adjustableViews.size() == 0)
            return;
        View adjustableView = adjustableViews.get(focusedEditText);
        if (adjustableView != null) {
            adjustableView.layout(adjustableView.getLeft(), adjustableView.getTop() - offsetUp,
                    adjustableView.getRight(), adjustableView.getBottom() - offsetUp);
        }
    }

    private void notifyKeyboardStateChanged(int height) {
        if (onChangedListener != null && focusedEditText != null) {
            View adjustableView = adjustableViews.get(focusedEditText);
            onChangedListener.onChanged(adjustableView, height);
        }
    }

    private OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v instanceof EditText) {
                if (focusedEditText == null) {
                    focusedEditText = (EditText) v;
                    resize = true;
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

    public void setOnChangedListener(OnChangedListener onChangedListener) {
        this.onChangedListener = onChangedListener;
    }

    public void setAutoResize(boolean autoResize) {
        this.autoResize = autoResize;
    }

    public interface OnChangedListener {
        void onChanged(View focusedView, int height);
    }

    private static class AndroidBug5497Workaround {

        // For more information, see https://code.google.com/p/android/issues/detail?id=5497
        // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

        private static void assistActivity(ViewGroup childOfContent) {
            new AndroidBug5497Workaround(childOfContent);
        }

        private ViewGroup mChildOfContent;

        private AndroidBug5497Workaround(ViewGroup childOfContent) {
            mChildOfContent = childOfContent;
            mChildOfContent.getViewTreeObserver()
                    .addOnGlobalLayoutListener(
                            this::possiblyResizeChildOfContent);
        }

        private void possiblyResizeChildOfContent() {
            if (isImmersiveMode()) {
                ViewGroup.LayoutParams params = mChildOfContent.getLayoutParams();
                int usableHeightNow = computeUsableHeight();
                int usableHeightSansKeyboard = getUsableHeightSansKeyboard();
                if (usableHeightSansKeyboard <= 0
                        || usableHeightNow == usableHeightSansKeyboard) {
                    return;
                }
                if (usableHeightNow < usableHeightSansKeyboard) {
                    // keyboard probably just became visible
                    params.height = usableHeightNow;
                } else {
                    // keyboard probably just became hidden
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                }
                mChildOfContent.requestLayout();
            }
        }

        private int getUsableHeightSansKeyboard() {
            //每次都重新获取，应付高度发生变化的情况
            for (int i = 0; i < mChildOfContent.getChildCount(); i++) {
                View child = mChildOfContent.getChildAt(i);
                ViewGroup.LayoutParams params = child.getLayoutParams();
                if (params.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                    return child.getHeight();
                }
            }
            return 0;
        }

        private int computeUsableHeight() {
            Rect r = new Rect();
            mChildOfContent.getWindowVisibleDisplayFrame(r);
            return r.bottom;
        }

        private boolean isImmersiveMode() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                int systemUiVisibility = ((Activity) mChildOfContent.getContext()).getWindow()
                        .getDecorView().getSystemUiVisibility();
                return !mChildOfContent.getFitsSystemWindows()
                        && (systemUiVisibility & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                        == View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            }
            return false;
        }
    }
}

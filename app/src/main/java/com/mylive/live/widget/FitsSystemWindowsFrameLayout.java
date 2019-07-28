package com.mylive.live.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

/**
 * 解决add多个Fragment后，除第一个外，其余的fitsSystemWindows都失效的问题
 * Create by zailongshi on 2019/7/28
 */
public class FitsSystemWindowsFrameLayout extends FrameLayout {
    public FitsSystemWindowsFrameLayout(@NonNull Context context) {
        super(context);
    }

    public FitsSystemWindowsFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FitsSystemWindowsFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs,
                                        int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        WindowInsets result = super.dispatchApplyWindowInsets(insets);
        if (!insets.isConsumed()) {
            final int count = getChildCount();
            for (int i = 0; i < count; i++)
                result = getChildAt(i).dispatchApplyWindowInsets(insets);
        }
        return result;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        ViewCompat.requestApplyInsets(child);
    }
}

package com.mylive.live.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 为了解决更换状态栏颜色造成闪烁的问题，所以使用此法
 * Create by zailongshi on 2019/7/28
 */
public class StatusBarMaskView extends View {

    private Rect outRect = new Rect();

    public StatusBarMaskView(Context context) {
        super(context);
    }

    public StatusBarMaskView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusBarMaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setZ(Float.MAX_VALUE);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        getWindowVisibleDisplayFrame(outRect);
        int statusBarHeight = outRect.top;
        setMeasuredDimension(width, statusBarHeight);
    }
}

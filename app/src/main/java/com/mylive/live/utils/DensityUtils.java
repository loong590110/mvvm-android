package com.mylive.live.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Create by zailongshi on 2019/8/3
 */
public final class DensityUtils {
    public static int dp2px(Context context, float dpValue) {
        return (int) (dpValue * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static float px2dp(Context context, int pxValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                pxValue, context.getResources().getDisplayMetrics());
    }
}

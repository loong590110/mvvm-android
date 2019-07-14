package com.mylive.live.utils;

import android.content.Context;
import androidx.annotation.StringRes;
import android.widget.Toast;

/**
 * Created by Developer Zailong Shi on 2019-06-19.
 */
public class ToastUtils {

    public static void showShortToast(Context context, String text) {
        if (context == null)
            return;
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showShortToast(Context context, @StringRes int resId) {
        if (context == null)
            return;
        showShortToast(context, context.getString(resId));
    }
}

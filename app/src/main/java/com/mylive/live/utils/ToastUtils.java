package com.mylive.live.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Developer Zailong Shi on 2019-06-19.
 */
public class ToastUtils {

    public static void showShortToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}

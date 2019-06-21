package com.mylive.live.core.starter;

import android.support.v4.app.FragmentActivity;

/**
 * Created by Developer Zailong Shi on 2018/12/21.
 */
public interface Starter {
    Finisher start(FragmentActivity context);

    void startForResult(FragmentActivity context, int requestCode);
}

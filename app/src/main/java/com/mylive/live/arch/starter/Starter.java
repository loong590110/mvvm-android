package com.mylive.live.arch.starter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Developer Zailong Shi on 2018/12/21.
 */
public interface Starter {
    Finisher start(FragmentActivity context);

    Finisher start(Fragment fragment);

    void startForResult(FragmentActivity context, int requestCode);

    void startForResult(Fragment fragment, int requestCode);
}

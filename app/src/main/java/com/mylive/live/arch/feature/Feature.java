package com.mylive.live.arch.feature;

import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by Developer Zailong Shi on 2019-07-01.
 */
public class Feature implements LifecycleObserver {

    private LifecycleOwner lifecycleOwner;

    public Feature(FeaturesActivity activity) {
        this.lifecycleOwner = activity;
        init();
    }

    public Feature(FeaturesFragment fragment) {
        this.lifecycleOwner = fragment;
        init();
    }

    private void init() {
        if (lifecycleOwner != null) {
            lifecycleOwner.getLifecycle().addObserver(this);
        }
    }

    public FeaturesActivity getActivity() {
        if (lifecycleOwner instanceof FeaturesActivity) {
            return (FeaturesActivity) lifecycleOwner;
        } else if (lifecycleOwner instanceof FeaturesFragment) {
            return (FeaturesActivity) ((FeaturesFragment) lifecycleOwner).getActivity();
        }
        return null;
    }

    public Context getContext() {
        return getActivity();
    }

    public Resources getResources() {
        return getContext().getResources();
    }

    public <T extends View> T findViewById(@IdRes int id) {
        //noinspection unchecked
        return (T) getActivity().findViewById(id);
    }

    public void startActivity(Intent intent) {
        startActivity(intent, null);
    }

    public void startActivity(Intent intent, Bundle options) {
        if (lifecycleOwner instanceof FeaturesActivity) {
            if (options != null) {
                intent = intent.putExtras(options);
            }
            ((FeaturesActivity) lifecycleOwner).startActivity(intent);
        } else if (lifecycleOwner instanceof FeaturesFragment) {
            ((FeaturesFragment) lifecycleOwner).startActivity(intent, options);
        }
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode, null);
    }

    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        if (lifecycleOwner instanceof FeaturesActivity) {
            if (options != null) {
                intent = intent.putExtras(options);
            }
            ((FeaturesActivity) lifecycleOwner).startActivityForResult(
                    intent, requestCode, options);
        } else if (lifecycleOwner instanceof FeaturesFragment) {
            ((FeaturesFragment) lifecycleOwner).startActivityForResult(
                    intent, requestCode, options);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
    }
}

package com.mylive.live.arch.feature;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import com.mylive.live.arch.mapper.Mapper;

import java.util.Objects;

/**
 * Created by Developer Zailong Shi on 2019-07-01.
 */
public class Feature implements LifecycleObserver {

    private LifecycleOwner lifecycleOwner;
    private Bundle arguments;

    public Feature(FeaturesActivity activity) {
        Objects.requireNonNull(activity);
        this.lifecycleOwner = activity;
        onConstructing();
        onAttach();
    }

    public Feature(FeaturesFragment fragment) {
        Objects.requireNonNull(fragment);
        this.lifecycleOwner = fragment;
        onConstructing();
        onAttach();
    }

    protected void onConstructing() {
        Mapper.from(lifecycleOwner).to(this);
    }

    protected void onAttach() {
        lifecycleOwner.getLifecycle().addObserver(this);
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

    public LifecycleOwner getLifecycleOwner() {
        return lifecycleOwner;
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

    public void onSaveInstanceState(@NonNull Bundle outState) {
        //noinspection ConstantConditions
        if (outState != null) {
            outState.putBundle("arguments", arguments);
        }
    }

    public void onRestoreInstanceState(@NonNull Bundle outState) {
        //noinspection ConstantConditions
        if (outState != null) {
            arguments = outState.getBundle("arguments");
        }
    }

    public Bundle getArguments() {
        return arguments;
    }

    public void setArguments(Bundle arguments) {
        this.arguments = arguments;
    }
}

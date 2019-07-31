package com.mylive.live.arch.feature;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.mylive.live.arch.mapper.Mapper;

import java.util.Objects;

/**
 * Created by Developer Zailong Shi on 2019-07-01.
 */
public class Feature implements LifecycleObserver {

    private FeaturesManagerOwner owner;
    private Bundle arguments;

    public Feature(FeaturesManagerOwner owner) {
        Objects.requireNonNull(owner);
        this.owner = owner;
        if (owner instanceof Activity) {
            if (!(owner instanceof FragmentActivity)) {
                throw new IllegalArgumentException("Only support FragmentActivity.");
            }
        }
        onConstructing();
        onAttach();
    }

    protected void onConstructing() {
        mapperFields();
    }

    protected void onAttach() {
        if (owner instanceof Activity) {
            owner.getLifecycle().addObserver(this);
        } else if (owner instanceof Fragment) {
            onViewLifecycleCreated(false);
        }
    }

    private void mapperFields() {
        Mapper.from(owner).to(this);
    }

    public final void onViewLifecycleCreated(boolean mapper) {
        if (getViewLifecycleOwner() != null) {
            if (mapper) {
                mapperFields();
            }
            getViewLifecycleOwner().getLifecycle().addObserver(this);
        }
    }

    public FeaturesManagerOwner getFeaturesManagerOwner() {
        return owner;
    }

    public LifecycleOwner getLifecycleOwner() {
        return owner;
    }

    public LifecycleOwner getViewLifecycleOwner() {
        if (owner instanceof Fragment) {
            return ((Fragment) owner).getViewLifecycleOwner();
        }
        return null;
    }

    public FragmentActivity getActivity() {
        if (owner instanceof FragmentActivity) {
            return (FragmentActivity) owner;
        } else if (owner instanceof Fragment) {
            return ((Fragment) owner).getActivity();
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
        if (owner instanceof FragmentActivity) {
            if (options != null) {
                intent = intent.putExtras(options);
            }
            ((FragmentActivity) owner).startActivity(intent);
        } else if (owner instanceof Fragment) {
            ((Fragment) owner).startActivity(intent, options);
        }
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode, null);
    }

    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        if (owner instanceof FragmentActivity) {
            if (options != null) {
                intent = intent.putExtras(options);
            }
            ((FragmentActivity) owner).startActivityForResult(intent, requestCode);
        } else if (owner instanceof Fragment) {
            ((Fragment) owner).startActivityForResult(intent, requestCode, options);
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

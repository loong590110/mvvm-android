package com.mylive.live.arch.feature;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * Created by Developer Zailong Shi on 2019-07-01.
 */
@SuppressLint("Registered")
public class FeaturesFragment extends Fragment implements FeaturesManagerOwner {

    private FeaturesManager featuresManager;
    private boolean hasFeaturesCreated;

    @Override
    public FeaturesManager getFeaturesManager() {
        if (featuresManager == null) {
            featuresManager = FeaturesManager.of(this);
        }
        return featuresManager;
    }

    /**
     * 为了@FieldMap注解能够同步create周期初始化的字段，
     * Feature类在start周期才开始被创建对象。
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onFeatureCreate() {
        if (!hasFeaturesCreated) {
            hasFeaturesCreated = true;
            FeaturesFinder.findEach(getClass(), clazz -> {
                getFeaturesManager().add(clazz);
            });
        } else {
            if (featuresManager != null) {
                for (Feature feature : featuresManager) {
                    feature.onViewLifecycleCreated(true);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (featuresManager != null) {
            for (Feature feature : featuresManager) {
                feature.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (featuresManager != null) {
            for (Feature feature : featuresManager) {
                feature.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (featuresManager != null) {
            for (Feature feature : featuresManager) {
                feature.onSaveInstanceState(outState);
            }
        }
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (featuresManager != null) {
            for (Feature feature : featuresManager) {
                feature.onRestoreInstanceState(savedInstanceState);
            }
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        onRestoreInstanceState(savedInstanceState);
    }
}

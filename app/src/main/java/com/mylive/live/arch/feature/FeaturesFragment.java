package com.mylive.live.arch.feature;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.Iterator;

/**
 * Created by Developer Zailong Shi on 2019-07-01.
 */
@SuppressLint("Registered")
public class FeaturesFragment extends Fragment {

    private FeaturesManager featuresManager;

    public FeaturesManager getFeaturesManager() {
        if (featuresManager == null) {
            featuresManager = FeaturesManager.of(this);
        }
        return featuresManager;
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
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (featuresManager != null) {
            for (Feature feature : featuresManager) {
                feature.onRestoreInstanceState(savedInstanceState);
            }
        }
    }
}

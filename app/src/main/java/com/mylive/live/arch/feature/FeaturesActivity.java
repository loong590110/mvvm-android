package com.mylive.live.arch.feature;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Developer Zailong Shi on 2019-07-01.
 */
@SuppressLint("Registered")
public class FeaturesActivity extends FragmentActivity {

    private FeaturesManager featuresManager;

    public FeaturesManager getFeaturesManager() {
        if (featuresManager == null) {
            featuresManager = FeaturesManager.of(this);
        }
        return featuresManager;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (featuresManager != null) {
            while (featuresManager.iterator().hasNext()) {
                featuresManager.iterator().next().onActivityResult(
                        requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (featuresManager != null) {
            while (featuresManager.iterator().hasNext()) {
                featuresManager.iterator().next().onRequestPermissionsResult(
                        requestCode, permissions, grantResults);
            }
        }
    }
}

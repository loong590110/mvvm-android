package com.mylive.live.arch.feature;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

/**
 * Created by Developer Zailong Shi on 2019-07-15.
 */
public interface FeaturesManagerOwner extends LifecycleOwner {
    FeaturesManager getFeaturesManager();

    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                    @NonNull int[] grantResults);
}

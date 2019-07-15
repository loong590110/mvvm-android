package com.mylive.live.arch.feature;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Developer Zailong Shi on 2019-07-15.
 */
public interface FeaturesManagerOwner extends LifecycleOwner {
    FeaturesManager getFeaturesManager();

    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                    @NonNull int[] grantResults);

    void onSaveInstanceState(@NonNull Bundle outState);

    void onRestoreInstanceState(@Nullable Bundle savedInstanceState);
}

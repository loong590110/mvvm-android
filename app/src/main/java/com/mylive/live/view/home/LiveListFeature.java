package com.mylive.live.view.home;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;

import com.mylive.live.arch.annotation.FieldMap;
import com.mylive.live.arch.feature.FeaturesFragment;
import com.mylive.live.base.BaseFeature;
import com.mylive.live.databinding.FragmentHomeBinding;

/**
 * Created by Developer Zailong Shi on 2019-07-01.
 */
public class LiveListFeature extends BaseFeature {

    @FieldMap("binding")
    FragmentHomeBinding binding;

    public LiveListFeature(FeaturesFragment fragment) {
        super(fragment);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onViewCreated() {
        binding.txtTitle.setText("hello, feature.");
    }
}

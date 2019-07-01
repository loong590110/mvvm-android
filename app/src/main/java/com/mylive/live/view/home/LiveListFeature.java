package com.mylive.live.view.home;

import com.mylive.live.arch.annotation.FieldMap;
import com.mylive.live.arch.feature.FeaturesFragment;
import com.mylive.live.arch.mapper.Mapper;
import com.mylive.live.arch.mvvm.CommunicableFeature;
import com.mylive.live.databinding.FragmentHomeBinding;

/**
 * Created by Developer Zailong Shi on 2019-07-01.
 */
public class LiveListFeature extends CommunicableFeature {

    @FieldMap("binding")
    FragmentHomeBinding binding;

    public LiveListFeature(FeaturesFragment fragment) {
        super(fragment);
        Mapper.from(fragment).to(this);
        binding.txtHome.setText("hello, feature.");
    }
}

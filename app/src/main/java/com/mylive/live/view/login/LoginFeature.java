package com.mylive.live.view.login;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;

import com.mylive.live.arch.annotation.FieldMap;
import com.mylive.live.arch.feature.FeaturesActivity;
import com.mylive.live.arch.feature.FeaturesFragment;
import com.mylive.live.arch.feature.FeaturesManagerOwner;
import com.mylive.live.base.BaseFeature;
import com.mylive.live.databinding.ActivityLoginBinding;

/**
 * Create by zailongshi on 2019/7/7
 */
public class LoginFeature extends BaseFeature {

    @FieldMap("binding")
    private ActivityLoginBinding binding;

    public LoginFeature(FeaturesManagerOwner owner) {
        super(owner);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onCreate() {
        binding.navigationBar.setRightButtonText("注册");
        binding.navigationBar.setOnRightButtonClickListener(v -> {

        });
    }
}

package com.mylive.live.view.login;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import com.mylive.live.arch.annotation.FieldMap;
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

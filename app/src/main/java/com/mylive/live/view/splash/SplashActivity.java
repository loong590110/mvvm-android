package com.mylive.live.view.splash;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.mylive.live.R;
import com.mylive.live.core.base.BaseActivity;
import com.mylive.live.databinding.ActivitySplashBinding;
import com.mylive.live.router.MainActivityStarter;
import com.mylive.live.viewmodel.SplashViewModel;

/**
 * Created by Developer Zailong Shi on 2019-06-19.
 */
public class SplashActivity extends BaseActivity {

    private ActivitySplashBinding binding;
    private SplashViewModel splashViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        splashViewModel = ViewModelProviders.of(this).get(SplashViewModel.class);
        splashViewModel.getTestData().observe(this, config -> {
            binding.txtTest.setText(config);
            splashViewModel.startCountDownTimer().observe(this, integer -> {
                if (integer != null && integer == 0) {
                    new MainActivityStarter().start(this).finish();
                } else {
                    binding.txtTimer.setText(String.valueOf(integer));
                }
            });
        });
    }
}

package com.mylive.live.view.splash;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.mylive.live.R;
import com.mylive.live.arch.permission.PermissionsRequester;
import com.mylive.live.base.BaseActivity;
import com.mylive.live.databinding.ActivitySplashBinding;
import com.mylive.live.router.MainActivityStarter;
import com.mylive.live.utils.ToastUtils;
import com.mylive.live.viewmodel.SplashViewModel;

/**
 * Created by Developer Zailong Shi on 2019-06-19.
 */
public class SplashActivity extends BaseActivity {

    private ActivitySplashBinding binding;
    private SplashViewModel splashViewModel;
    private PermissionsRequester requester;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        splashViewModel = ViewModelProviders.of(this).get(SplashViewModel.class);
        requester = PermissionsRequester.create(this, 100)
                .addHandler(new PermissionsRequester.RequestResultHandler() {
                    @Override
                    protected void onAllEssentialPermissionsAreGranted() {
                        splashViewModel.getConfig().observe(SplashActivity.this, config -> {
                            binding.txtTest.append(JSON.toJSONString(config) + "\n\n");
                            splashViewModel.startCountDownTimer().observe(SplashActivity.this,
                                    integer -> {
                                        if (integer != null && integer == 0) {
                                            new MainActivityStarter()
                                                    .start(SplashActivity.this)
                                                    .finish();
                                        } else {
                                            binding.txtTimer.setText(String.valueOf(integer));
                                        }
                                    });
                        });
                        splashViewModel.getTestData().observe(SplashActivity.this, s -> {
                            if (s != null && s.length() > 1024)
                                s = s.substring(0, 1024);
                            binding.txtTest.append(s + "\n\n");
                        });
                    }

                    @Override
                    protected boolean shouldShowRequestPermissionRationale(String permission) {
                        ToastUtils.showShortToast(SplashActivity.this, "rationale");
                        requester.request();
                        return true;
                    }

                    @Override
                    protected boolean onPermissionIsDeniedAndNeverAsks(String permission) {
                        ToastUtils.showShortToast(SplashActivity.this, "denied and never asks");
                        return true;
                    }
                })
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        requester.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

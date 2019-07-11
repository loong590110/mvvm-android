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
import com.mylive.live.utils.DoubleClickExit;
import com.mylive.live.databinding.ActivitySplashBinding;
import com.mylive.live.dialog.AlertDialog;
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
                                            MainActivityStarter.create()
                                                    .start(SplashActivity.this)
                                                    .finish();
                                        } else {
                                            binding.txtTimer.setText(String.valueOf(integer));
                                        }
                                    });
                        });
                        splashViewModel.getTestData().observe(SplashActivity.this, s -> {
                            if (s != null && s.length() > 256) {
                                s = s.substring(0, 256);
                            }
                            binding.txtTest.append(s + "\n\n");
                        });
                    }

                    @Override
                    protected boolean shouldShowRequestPermissionRationale(String permission) {
                        new AlertDialog.Builder(SplashActivity.this)
                                .setMessage("应用需要存储权限来存储运行必须的数据")
                                .setCancelText("暂时拒绝")
                                .setConfirmText("重新申请")
                                .setOnConfirmClickListener((dialog, which) -> {
                                    requester.request();
                                })
                                .show();
                        return true;
                    }

                    @Override
                    protected boolean onPermissionIsDeniedAndNeverAsks(String permission) {
                        new AlertDialog.Builder(SplashActivity.this)
                                .setMessage("应用需要存储权限来存储运行必须的数据")
                                .setCancelText("不再提醒")
                                .setConfirmText("前往设置")
                                .setOnConfirmClickListener((dialog, which) -> {
                                    //open settings
                                })
                                .show();
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

    @Override
    public void onBackPressed() {
        if (!DoubleClickExit.getInstance().onBackPressed()) {
            ToastUtils.showShortToast(this, R.string.double_click_exit_app);
            return;
        }
        super.onBackPressed();
    }
}

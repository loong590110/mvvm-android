package com.mylive.live.view.splash;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.mylive.live.R;
import com.mylive.live.arch.permission.PermissionsRequester;
import com.mylive.live.base.BaseActivity;
import com.mylive.live.databinding.ActivitySplashBinding;
import com.mylive.live.dialog.AlertDialog;
import com.mylive.live.router.MainActivityStarter;
import com.mylive.live.utils.DoubleClickExit;
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
        binding.progressCircular.setVisibility(View.INVISIBLE);
        splashViewModel = ViewModelProviders.of(this).get(SplashViewModel.class);
        requester = PermissionsRequester.create(this, 100)
                .addHandler(new PermissionsRequester.RequestResultHandler() {
                    @Override
                    protected void onAllEssentialPermissionsAreGranted() {
                        splashViewModel.getConfig().observe(SplashActivity.this, config -> {
                            splashViewModel.startCountDownTimer().observe(
                                    SplashActivity.this,
                                    tick -> {
                                        if (binding.progressCircular.getMax() == 100) {
                                            binding.progressCircular.setMax(tick);
                                            binding.progressCircular.setOnClickListener(v -> {
                                                startNextActivity();
                                            });
                                        }
                                        binding.progressCircular.setVisibility(View.VISIBLE);
                                        binding.progressCircular.setProgress(tick);
                                        if (tick == 0) {
                                            startNextActivity();
                                        }
                                    });
                        });
                    }

                    @Override
                    protected boolean shouldShowRequestPermissionRationale(String permission) {
                        new AlertDialog.Builder(SplashActivity.this)
                                .setMessage("应用需要存储权限来存储运行必须的数据")
                                .setCancelText("暂时拒绝")
                                .setConfirmText("重新申请")
                                .setOnConfirmClickListener((dialog, which) -> {
                                    dialog.dismiss();
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
                                    dialog.dismiss();
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

    private void startNextActivity() {
        MainActivityStarter.create()
                .start(SplashActivity.this)
                .finish();
    }
}

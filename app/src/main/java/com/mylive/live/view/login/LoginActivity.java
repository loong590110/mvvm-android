package com.mylive.live.view.login;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.mylive.live.R;
import com.mylive.live.arch.annotation.Features;
import com.mylive.live.arch.annotation.FieldMap;
import com.mylive.live.base.BaseActivity;
import com.mylive.live.databinding.ActivityLoginBinding;

/**
 * Create by zailongshi on 2019/7/7
 */
@Features(LoginFeature.class)
public class LoginActivity extends BaseActivity {

    @FieldMap("binding")
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
    }
}

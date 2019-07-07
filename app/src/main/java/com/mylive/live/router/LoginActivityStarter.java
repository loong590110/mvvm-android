package com.mylive.live.router;

import com.mylive.live.arch.feature.ActivityStarter;
import com.mylive.live.view.login.LoginActivity;

/**
 * Create by zailongshi on 2019/7/7
 */
public final class LoginActivityStarter extends ActivityStarter<LoginActivity> {

    public static LoginActivityStarter create() {
        return new LoginActivityStarter();
    }

    private LoginActivityStarter() {
    }
}
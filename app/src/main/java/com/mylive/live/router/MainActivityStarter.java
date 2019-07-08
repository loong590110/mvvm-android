package com.mylive.live.router;

import com.mylive.live.arch.feature.FeaturesActivityStarter;
import com.mylive.live.view.main.MainActivity;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public final class MainActivityStarter extends FeaturesActivityStarter<MainActivity> {

    public static MainActivityStarter create() {
        return new MainActivityStarter();
    }

    private MainActivityStarter() {
    }
}

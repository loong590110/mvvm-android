package com.mylive.live.arch.feature;

import com.mylive.live.arch.starter.Finisher;

/**
 * Created by Developer Zailong Shi on 2018/12/21.
 */
public interface Starter {
    Finisher start(Feature feature);

    void startForResult(Feature feature, int requestCode);
}

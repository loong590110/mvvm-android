package com.mylive.live.router;

import com.mylive.live.arch.feature.FeaturesActivityStarter;
import com.mylive.live.view.web.WebActivity;

/**
 * Created by Developer Zailong Shi on 2019-07-09.
 */
public final class WebActivityStarter extends FeaturesActivityStarter<WebActivity> {

    public static WebActivityStarter create(String url) {
        return new WebActivityStarter(url);
    }

    private WebActivityStarter(String url) {
        intent.putExtra("url", url);
    }
}

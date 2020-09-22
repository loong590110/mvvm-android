package com.mylive.live.router;

import com.mylive.live.arch.feature.FeaturesActivityStarter;
import com.mylive.live.view.room.PlayerActivity;

public class PlayerActivityStarter extends FeaturesActivityStarter<PlayerActivity> {
    public static PlayerActivityStarter create(String uri) {
        return new PlayerActivityStarter(uri);
    }

    private PlayerActivityStarter(String uri) {
        intent.putExtra("uri", uri);
    }
}

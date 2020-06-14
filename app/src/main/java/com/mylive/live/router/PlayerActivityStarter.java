package com.mylive.live.router;

import com.mylive.live.arch.feature.FeaturesActivityStarter;
import com.mylive.live.view.room.PlayerActivity;

public class PlayerActivityStarter extends FeaturesActivityStarter<PlayerActivity> {
    public static PlayerActivityStarter create() {
        return new PlayerActivityStarter();
    }

    private PlayerActivityStarter() {
    }
}

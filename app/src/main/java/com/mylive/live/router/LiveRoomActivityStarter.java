package com.mylive.live.router;

import com.mylive.live.arch.feature.FeaturesActivityStarter;
import com.mylive.live.view.room.LiveRoomActivity;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public final class LiveRoomActivityStarter extends FeaturesActivityStarter<LiveRoomActivity> {

    public static LiveRoomActivityStarter create() {
        return new LiveRoomActivityStarter();
    }

    private LiveRoomActivityStarter() {
    }
}

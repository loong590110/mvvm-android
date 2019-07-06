package com.mylive.live.router;

import com.mylive.live.arch.starter.ActivityStarter;
import com.mylive.live.view.room.LiveRoomActivity;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public final class LiveRoomActivityStarter extends ActivityStarter<LiveRoomActivity> {

    public static LiveRoomActivityStarter create() {
        return new LiveRoomActivityStarter();
    }

    private LiveRoomActivityStarter() {
    }
}

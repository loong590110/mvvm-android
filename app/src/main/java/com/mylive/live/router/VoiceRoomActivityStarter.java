package com.mylive.live.router;

import com.mylive.live.arch.feature.FeaturesActivityStarter;
import com.mylive.live.view.room.VoiceRoomActivity;

/**
 * Created by Developer Zailong Shi on 2019-06-20.
 */
public final class VoiceRoomActivityStarter extends FeaturesActivityStarter<VoiceRoomActivity> {

    public static VoiceRoomActivityStarter create() {
        return new VoiceRoomActivityStarter();
    }

    private VoiceRoomActivityStarter() {
    }
}

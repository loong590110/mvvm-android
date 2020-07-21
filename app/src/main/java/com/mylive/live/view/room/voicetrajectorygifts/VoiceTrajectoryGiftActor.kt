package com.mylive.live.view.room.voicetrajectorygifts

import android.view.View
import java.lang.ref.WeakReference

/**
 * Created by Developer Zailong Shi on 2020/7/21.
 */
class VoiceTrajectoryGiftActor(giftView: View, script: VoiceTrajectoryGiftScript) : Actor(script) {
    private val giftViewRef = WeakReference<View>(giftView)

    override fun perform() {
        (script as VoiceTrajectoryGiftScript).perform(this)
    }
}
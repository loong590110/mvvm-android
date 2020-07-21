package com.mylive.live.view.room.voicetrajectorygifts

import android.view.View

/**
 * Created by Developer Zailong Shi on 2020/7/21.
 */
object VoiceTrajectoryGiftsDirector : Director {
    private val backstage by lazy { Backstage(100) }
    private val stage by lazy { VoiceTrajectoryGiftsStage(100) }
    override fun direct() {
//        val actor = backstage.get()
//                ?: VoiceTrajectoryGiftActor(View(null), VoiceTrajectoryGiftScript())
//        stage.put(actor)?.perform()
    }
}
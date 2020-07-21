package com.mylive.live.view.room.voicetrajectorygifts

/**
 * Created by Developer Zailong Shi on 2020/7/21.
 */
interface Script<T : Actor> {
    fun perform(actor: T)
}
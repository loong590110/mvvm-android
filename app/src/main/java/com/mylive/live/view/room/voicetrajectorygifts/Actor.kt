package com.mylive.live.view.room.voicetrajectorygifts

/**
 * Created by Developer Zailong Shi on 2020/7/21.
 */
abstract class Actor(protected val script: Script<*>) {
    abstract fun perform()
}
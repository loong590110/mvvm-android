package com.mylive.live.view.room.voicetrajectorygifts

import java.util.concurrent.LinkedBlockingQueue

/**
 * Created by Developer Zailong Shi on 2020/7/21.
 */
class Backstage(capacity: Int = Int.MAX_VALUE) {
    private val actors by lazy { LinkedBlockingQueue<Actor>(capacity) }

    fun put(actor: Actor): Actor? {
        if (actors.remainingCapacity() > 0) {
            actors.put(actor)
            return actor
        }
        return null
    }

    fun get(): Actor? = actors.peek()
}
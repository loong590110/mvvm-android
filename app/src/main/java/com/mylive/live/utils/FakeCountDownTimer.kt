package com.mylive.live.utils

import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import java.util.concurrent.atomic.AtomicInteger

abstract class FakeCountDownTimer(private val startCount: Int, private val interval: Long) {
    private val count by lazy { AtomicInteger() }
    private val offset by lazy { AtomicInteger() }
    private val realCountDownTimer by lazy {
        object : CountDownTimer(startCount * interval, interval) {
            override fun onTick(millisUntilFinished: Long) {
                (millisUntilFinished / interval).toInt().let {
                    if (offset.get() == -1) {
                        offset.set(startCount - it)
                    }
                    count.set(it)
                }
                this@FakeCountDownTimer.onTick(count.get() + offset.get())
            }

            override fun onFinish() {
                if (count.get() + offset.get() <= 1) {
                    started = false
                    this@FakeCountDownTimer.onFinish()
                } else {
                    this@FakeCountDownTimer.onTick(1)
                    (uiHandler ?: Handler(Looper.getMainLooper()).also { uiHandler = it })
                            .postDelayed({
                                if (started) {
                                    started = false
                                    this@FakeCountDownTimer.onFinish()
                                }
                            }, interval)
                }
            }
        }
    }

    @Volatile
    private var started = false
    private var uiHandler: Handler? = null
    abstract fun onTick(count: Int)
    abstract fun onFinish()

    @Synchronized
    fun cancel() {
        if (started) {
            started = false
            uiHandler?.removeCallbacksAndMessages(null)
            realCountDownTimer.cancel()
        }
    }

    @Synchronized
    fun start(): FakeCountDownTimer {
        started = true
        offset.set(-1)
        realCountDownTimer.start()
        return this
    }
}
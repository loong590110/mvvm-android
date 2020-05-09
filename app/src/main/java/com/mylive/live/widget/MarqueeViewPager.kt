package com.mylive.live.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.RequiresApi

/**
 * Created by Developer Zailong Shi on 2020/5/9.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class MarqueeViewPager(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
    : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?)
            : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)
}
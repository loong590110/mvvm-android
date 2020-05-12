package com.mylive.live.widget

import android.content.Context
import android.database.DataSetObservable
import android.database.DataSetObserver
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresApi

/**
 * Created by Developer Zailong Shi on 2020/5/9.
 */
class MarqueeViewPager(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
    : FrameLayout(context, attrs, defStyleAttr) {
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private var viewHolders: Array<ViewHolder>? = null
    private var adapter: Adapter<*>? = null

    var currentPosition: Int = -1
        set(value) {
            adapter?.apply {
                val itemCount = getItemCount()
                var new = value
                if (new < 0) {
                    new = 0
                } else if (new >= itemCount) {
                    new = itemCount - 1
                }
                field = new
                createViewHolders(this@MarqueeViewPager)
                bindViewHolders(field, itemCount)
            }
        }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        viewHolders?.forEach {
            it.apply {
                when (location) {
                    Location.BEHIND -> itemView.apply {
                        translationX = 0f
                        scaleX = 0.5f
                        scaleY = 0.5f
                    }
                    Location.LEFT -> itemView.apply {
                        translationX = -1f * measuredWidth * 0.7f
                        scaleX = 0.75f
                        scaleY = 0.75f
                    }
                    Location.RIGHT -> itemView.apply {
                        translationX = 1f * measuredWidth * 0.7f
                        scaleX = 0.75f
                        scaleY = 0.75f
                    }
                    Location.FRONT -> itemView.apply {
                        translationX = 0f
                        scaleX = 1f
                        scaleY = 1f
                    }
                }
            }
        }
    }

    fun getAdapter(): Adapter<*>? {
        return adapter
    }

    fun <T : ViewHolder> setAdapter(adapter: Adapter<T>) {
        if (this.adapter != adapter) {
            adapter.registerObserver(object : DataSetObserver() {
                override fun onInvalidated() {
                    onChanged()
                }

                override fun onChanged() {
                    viewHolders?.forEach {
                        it.position = -1
                    }
                    currentPosition = 0
                }
            })
        }
        adapter.notifyChanged()
        this.adapter = adapter
    }

    fun previous() {
        var prev = currentPosition - 1
        if (prev < 0) {
            adapter?.apply {
                val itemCount = getItemCount()
                if (itemCount > 1) {
                    prev = itemCount - 1
                    currentPosition = prev
                }
            }
        }
    }

    fun next() {
        adapter?.apply {
            val itemCount = getItemCount()
            if (itemCount > 1) {
                var next = currentPosition + 1
                if (next >= itemCount) {
                    next = 0
                }
                currentPosition = next
            }
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER
        )
    }

    class LayoutParams : FrameLayout.LayoutParams {
        constructor(c: Context, attrs: AttributeSet?) : super(c, attrs)
        constructor(width: Int, height: Int, gravity: Int) : super(width, height, gravity)
        constructor(width: Int, height: Int) : super(width, height)

        @RequiresApi(Build.VERSION_CODES.KITKAT)
        constructor(source: FrameLayout.LayoutParams) : super(source)
        constructor(source: ViewGroup.LayoutParams) : super(source)
        constructor(source: MarginLayoutParams) : super(source)
    }

    abstract class Adapter<T : ViewHolder> : DataSetObservable() {
        private var viewHolders: Array<ViewHolder>? = null

        internal fun createViewHolders(parent: MarqueeViewPager) {
            if (viewHolders == null) {
                viewHolders = Array(4) {
                    onCreateViewHolder(parent).apply {
                        when (it) {
                            0 -> location = Location.BEHIND
                            1 -> location = Location.LEFT
                            2 -> location = Location.RIGHT
                            3 -> location = Location.FRONT
                        }
                    }
                }
                parent.viewHolders = viewHolders
                viewHolders!!.forEach {
                    it.apply {
                        makeLayoutParams(parent, this)
                        if (itemView.parent == null) {
                            parent.addView(itemView)
                        } else if (itemView.parent != parent) {
                            (itemView.parent as ViewGroup).removeView(itemView)
                            parent.addView(itemView)
                        }
                    }
                }
            }
        }

        internal fun bindViewHolders(currentPosition: Int, itemCount: Int) {
            viewHolders!!.forEach {
                it.apply {
                    when (location) {
                        Location.FRONT -> {
                            if (currentPosition != position) {
                                itemView.alpha = 1f
                                bindViewHolder(this, currentPosition)
                            }
                        }
                        Location.LEFT -> {
                            if (itemCount <= 1) {
                                itemView.alpha = 0f
                                return
                            }
                            var leftPosition = currentPosition - 1
                            if (leftPosition < 0) {
                                leftPosition = itemCount - 1
                            }
                            if (leftPosition != position) {
                                itemView.alpha = 1f
                                bindViewHolder(this, leftPosition)
                            }
                        }
                        Location.RIGHT -> {
                            if (itemCount <= 1) {
                                itemView.alpha = 0f
                                return
                            }
                            var rightPosition = currentPosition + 1
                            if (rightPosition >= itemCount) {
                                rightPosition = 0
                            }
                            if (rightPosition != position) {
                                itemView.alpha = 1f
                                bindViewHolder(this, rightPosition)
                            }
                        }
                        else -> itemView.alpha = 0f
                    }
                }
            }
        }

        private fun bindViewHolder(viewHolder: ViewHolder, position: Int) {
            @Suppress("UNCHECKED_CAST")
            onBindViewHolder(viewHolder as T, position)
            viewHolder.position = position
        }

        private fun makeLayoutParams(parent: MarqueeViewPager, viewHolder: ViewHolder) {
            if (viewHolder.itemView.layoutParams == null) {
                viewHolder.itemView.layoutParams = parent.generateDefaultLayoutParams()
            } else if (viewHolder.itemView.layoutParams is FrameLayout.LayoutParams) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    viewHolder.itemView.layoutParams = LayoutParams(
                            viewHolder.itemView.layoutParams as FrameLayout.LayoutParams
                    ).apply {
                        gravity = Gravity.CENTER
                    }
                } else {
                    viewHolder.itemView.layoutParams = LayoutParams(
                            viewHolder.itemView.layoutParams as MarginLayoutParams
                    ).apply {
                        gravity = Gravity.CENTER
                    }
                }
            } else if (viewHolder.itemView.layoutParams is MarginLayoutParams) {
                viewHolder.itemView.layoutParams = LayoutParams(
                        viewHolder.itemView.layoutParams as MarginLayoutParams
                ).apply {
                    gravity = Gravity.CENTER
                }
            } else if (viewHolder.itemView.layoutParams is ViewGroup.LayoutParams) {
                viewHolder.itemView.layoutParams = LayoutParams(
                        viewHolder.itemView.layoutParams
                ).apply {
                    gravity = Gravity.CENTER
                }
            }
        }

        abstract fun onCreateViewHolder(parent: ViewGroup): T
        abstract fun onBindViewHolder(holder: T, position: Int)
        abstract fun getItemCount(): Int
    }

    open class ViewHolder(val itemView: View) {
        var position: Int = -1
        var location: Location? = null
    }

    enum class Location {
        LEFT, RIGHT, FRONT, BEHIND
    }
}
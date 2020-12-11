package com.mylive.live.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ProgressBar
import com.mylive.live.R

class BevelProgressBar @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ProgressBar(context, attrs, defStyleAttr) {
    private var bevelDrawable: BevelDrawable? = null
    private var progressBevel = 0
    private var progressLeftTopRadius = 0
    private var progressLeftBottomRadius = 0
    private var progressGradientAngle = 0
    private var progressGradientType = 0
    private var progressStartColor = 0
    private var progressCenterColor = 0
    private var progressEndColor = 0
    private var left = false
    var onProgressClickListener: OnProgressClickListener? = null
        set(value) {
            setOnClickListener {
                field?.onProgressClick(left)
            }
            field = value
        }

    init {
        progressDrawable?.apply {
            if (this is LayerDrawable) {
                context.obtainStyledAttributes(
                        attrs, R.styleable.BevelProgressBar,
                        defStyleAttr, 0
                ).apply {
                    progressBevel = getDimensionPixelSize(
                            R.styleable.BevelProgressBar_progressBevel,
                            progressBevel
                    )
                    if (progressBevel != 0) {
                        progressLeftTopRadius = getDimensionPixelSize(
                                R.styleable.BevelProgressBar_progressLeftTopRadius,
                                progressLeftTopRadius
                        )
                        progressLeftBottomRadius = getDimensionPixelSize(
                                R.styleable.BevelProgressBar_progressLeftBottomRadius,
                                progressLeftBottomRadius
                        )
                        progressGradientAngle = getInt(
                                R.styleable.BevelProgressBar_progressGradientAngle,
                                progressGradientAngle
                        )
                        progressGradientType = getInt(
                                R.styleable.BevelProgressBar_progressGradientType,
                                progressGradientType
                        )
                        progressStartColor = getColor(
                                R.styleable.BevelProgressBar_progressStartColor,
                                progressStartColor
                        )
                        progressCenterColor = getColor(
                                R.styleable.BevelProgressBar_progressCenterColor,
                                progressCenterColor
                        )
                        progressEndColor = getColor(
                                R.styleable.BevelProgressBar_progressEndColor,
                                progressEndColor
                        )
                    }
                }.recycle()
                if (progressBevel != 0) {
                    val d = findDrawableByLayerId(android.R.id.progress)
                    setDrawableByLayerId(
                            android.R.id.progress,
                            BevelDrawable(
                                    d,
                                    progressBevel,
                                    progressLeftTopRadius,
                                    progressLeftBottomRadius,
                                    progressGradientAngle,
                                    progressGradientType,
                                    progressStartColor,
                                    progressCenterColor,
                                    progressEndColor
                            ).apply {
                                bevelDrawable = this
                                level = d.level
                            }
                    )
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                IntArray(2).let {
                    getLocationInWindow(it)
                    left = event.x < (it[0] + width) * progress / max
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        bevelDrawable?.setBounds(0, 0, measuredWidth, measuredHeight)
    }

    override fun setProgress(progress: Int) {
        super.setProgress(progress)
        postInvalidate()
    }

    override fun setProgress(progress: Int, animate: Boolean) {
        super.setProgress(progress, animate)
        postInvalidate()
    }

    private class BevelDrawable(
            var drawable: Drawable?,
            var progressBevel: Int,
            var progressLeftTopRadius: Int,
            var progressLeftBottomRadius: Int,
            var progressGradientAngle: Int,
            var progressGradientType: Int,
            var progressStartColor: Int,
            var progressCenterColor: Int,
            var progressEndColor: Int
    ) : Drawable() {
        private val path by lazy { Path() }
        private val paint by lazy {
            Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
                shader = LinearGradient(
                        rectF.left, rectF.top,
                        rectF.right, rectF.bottom,
                        colors, null,
                        Shader.TileMode.CLAMP
                )
            }
        }
        private val colors by lazy {
            arrayOf(
                    progressStartColor,
                    progressCenterColor,
                    progressEndColor
            ).filter {
                it != 0
            }.toIntArray().takeIf {
                it.isEmpty().not()
            }.let {
                it ?: intArrayOf(Color.CYAN, Color.CYAN)
            }
        }
        private val rectF by lazy {
            val width = if (progressGradientType == 0) {
                intrinsicWidth
            } else {
                intrinsicWidth * level / 10000
            }
            RectF(0f, 0f, 0f, 0f).apply {
                when (progressGradientAngle % 360) {
                    in 0..179 -> {
                        left = 0f
                        top = 0f
                        right = width.toFloat()
                        bottom = 0f
                    }
                    in 180..359 -> {
                        left = width.toFloat()
                        top = 0f
                        right = 0f
                        bottom = 0f
                    }
                }
            }
        }

        override fun draw(canvas: Canvas) {
            canvas.run {
                path.apply {
                    reset()//重置
                    val width = 1f * intrinsicWidth * level / 10000 + progressBevel / 2
                    if (progressLeftTopRadius > 0) {
                        arcTo(
                                RectF(
                                        0f, 0f,
                                        2f * progressLeftTopRadius,
                                        2f * progressLeftTopRadius
                                ),
                                180f, 90f
                        )
                    } else {
                        moveTo(0f, 0f)
                    }
                    lineTo(width, 0f)
                    lineTo(width - progressBevel, 1f * intrinsicHeight)
                    if (progressLeftBottomRadius > 0) {
                        arcTo(
                                RectF(
                                        0f,
                                        intrinsicHeight - 2f * progressLeftBottomRadius,
                                        2f * progressLeftBottomRadius,
                                        1f * intrinsicHeight
                                ),
                                90f, 90f
                        )
                    } else {
                        lineTo(0f, 1f * intrinsicHeight)
                    }
                    close()
                }
                if (progressGradientType == 1) {
                    paint.apply {
                        shader = LinearGradient(
                                rectF.left, rectF.top,
                                rectF.right, rectF.bottom,
                                colors, null,
                                Shader.TileMode.CLAMP
                        )
                    }
                }
                drawPath(path, paint)
            }
        }

        override fun getIntrinsicWidth(): Int {
            return bounds.right - bounds.left
        }

        override fun getIntrinsicHeight(): Int {
            return bounds.bottom - bounds.top
        }

        override fun setBounds(bounds: Rect) {
            super.setBounds(bounds)
            drawable?.bounds = bounds
        }

        override fun setAlpha(alpha: Int) {
            drawable?.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            drawable?.colorFilter = colorFilter
        }

        override fun getOpacity(): Int {
            return drawable?.opacity ?: 255
        }

        override fun getConstantState(): ConstantState? {
            return drawable?.constantState
        }
    }

    interface OnProgressClickListener {
        fun onProgressClick(left: Boolean)
    }
}
package com.mylive.live.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

/**
 * Create by zailongshi on 2019/8/3
 */
public class LoadingView extends FrameLayout {

    private final int defSize = (int) (60 * getResources().getDisplayMetrics().density + .5f);
    private final float bgRatio = 40f / 60, fgRatio = 26f / 60;
    private CircleImageView circleImageView;
    private ProgressBar progressBar;
    private ValueAnimator valueAnimator;
    private boolean loading;

    public LoadingView(@NonNull Context context) {
        this(context, null);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        circleImageView = new CircleImageView(context, Color.WHITE);
        circleImageView.setScaleX(0f);
        circleImageView.setScaleY(0f);
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
        );
        params.gravity = Gravity.CENTER;
        addView(circleImageView, params);
        progressBar = new ProgressBar(context, attrs, defStyleAttr);
        progressBar.setScaleX(0f);
        progressBar.setScaleY(0f);
        params = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
        );
        params.gravity = Gravity.CENTER;
        addView(progressBar, params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setZ(circleImageView.getZ() + 1);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(getSize(defSize, widthMeasureSpec),
                getSize(defSize, heightMeasureSpec));
        setMeasuredDimension(size, size);
        int measureSpec = MeasureSpec.makeMeasureSpec((int) (size * bgRatio),
                MeasureSpec.EXACTLY);
        circleImageView.measure(measureSpec, measureSpec);
        measureSpec = MeasureSpec.makeMeasureSpec((int) (size * fgRatio),
                MeasureSpec.EXACTLY);
        progressBar.measure(measureSpec, measureSpec);
    }

    private int getSize(int defSize, int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            return MeasureSpec.getSize(measureSpec);
        }
        return defSize;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        if (this.loading != loading) {
            this.loading = loading;
            if (loading) {
                startAnimation();
            } else {
                cancelAnimation();
            }
        }
    }

    @Override
    public void setBackgroundColor(int color) {
        circleImageView.setBackgroundColor(color);
    }

    @Override
    public void setBackgroundResource(int resId) {
        circleImageView.setBackgroundColorRes(resId);
    }

    @Override
    public void setBackground(Drawable background) {
        setBackgroundDrawable(background);
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        circleImageView.setBackgroundDrawable(background);
    }

    @Override
    public void setBackgroundTintList(@Nullable ColorStateList tint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            circleImageView.setBackgroundTintList(tint);
        }
    }

    public void setForegroundColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateTintList(new ColorStateList(
                    new int[][]{{0}},
                    new int[]{color}
            ));
        }
    }

    public void setForegroundColorRes(int colorRes) {
        setForegroundColor(getResources().getColor(colorRes));
    }

    private void startAnimation() {
        startAnimation(0.f, 1.f);
    }

    private void startAnimation(float start, float end) {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        valueAnimator = ValueAnimator.ofFloat(start, end);
        valueAnimator.addUpdateListener(animation -> {
            float s = (float) animation.getAnimatedValue();
            circleImageView.setScaleX(s);
            circleImageView.setScaleY(s);
            if (s < .25f) {
                progressBar.setScaleX(0f);
                progressBar.setScaleY(0f);
            } else {
                progressBar.setScaleX(s);
                progressBar.setScaleY(s);
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(500);
        valueAnimator.start();
    }

    private void cancelAnimation() {
        startAnimation(1.f, 0.f);
    }

    public class CircleImageView extends AppCompatImageView {
        private static final int KEY_SHADOW_COLOR = 0x1E000000;
        private static final int FILL_SHADOW_COLOR = 0x3D000000;
        // PX
        private static final float X_OFFSET = 0f;
        private static final float Y_OFFSET = 1.75f;
        private static final float SHADOW_RADIUS = 3.5f;
        private static final int SHADOW_ELEVATION = 4;

        private Animation.AnimationListener mListener;
        int mShadowRadius;

        public CircleImageView(Context context, int color) {
            super(context);
            final float density = getContext().getResources().getDisplayMetrics().density;
            final int shadowYOffset = (int) (density * Y_OFFSET);
            final int shadowXOffset = (int) (density * X_OFFSET);

            mShadowRadius = (int) (density * SHADOW_RADIUS);

            ShapeDrawable circle;
            if (elevationSupported()) {
                circle = new ShapeDrawable(new OvalShape());
                ViewCompat.setElevation(this, SHADOW_ELEVATION * density);
            } else {
                OvalShape oval = new OvalShadow(mShadowRadius);
                circle = new ShapeDrawable(oval);
                setLayerType(View.LAYER_TYPE_SOFTWARE, circle.getPaint());
                circle.getPaint().setShadowLayer(mShadowRadius, shadowXOffset, shadowYOffset,
                        KEY_SHADOW_COLOR);
                final int padding = mShadowRadius;
                // set padding so the inner image sits correctly within the shadow.
                setPadding(padding, padding, padding, padding);
            }
            circle.getPaint().setColor(color);
            ViewCompat.setBackground(this, circle);
        }

        private boolean elevationSupported() {
            return android.os.Build.VERSION.SDK_INT >= 21;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (!elevationSupported()) {
                setMeasuredDimension(getMeasuredWidth() + mShadowRadius * 2,
                        getMeasuredHeight() + mShadowRadius * 2);
            }
        }

        public void setAnimationListener(Animation.AnimationListener listener) {
            mListener = listener;
        }

        @Override
        public void onAnimationStart() {
            super.onAnimationStart();
            if (mListener != null) {
                mListener.onAnimationStart(getAnimation());
            }
        }

        @Override
        public void onAnimationEnd() {
            super.onAnimationEnd();
            if (mListener != null) {
                mListener.onAnimationEnd(getAnimation());
            }
        }

        /**
         * Update the background color of the circle image view.
         *
         * @param colorRes Id of a color resource.
         */
        public void setBackgroundColorRes(int colorRes) {
            setBackgroundColor(ContextCompat.getColor(getContext(), colorRes));
        }

        @Override
        public void setBackgroundColor(int color) {
            if (getBackground() instanceof ShapeDrawable) {
                ((ShapeDrawable) getBackground()).getPaint().setColor(color);
            }
        }

        private class OvalShadow extends OvalShape {
            private RadialGradient mRadialGradient;
            private Paint mShadowPaint;

            OvalShadow(int shadowRadius) {
                super();
                mShadowPaint = new Paint();
                mShadowRadius = shadowRadius;
                updateRadialGradient((int) rect().width());
            }

            @Override
            protected void onResize(float width, float height) {
                super.onResize(width, height);
                updateRadialGradient((int) width);
            }

            @Override
            public void draw(Canvas canvas, Paint paint) {
                final int viewWidth = CircleImageView.this.getWidth();
                final int viewHeight = CircleImageView.this.getHeight();
                canvas.drawCircle(viewWidth / 2f, viewHeight / 2f,
                        viewWidth / 2f, mShadowPaint);
                canvas.drawCircle(viewWidth / 2f, viewHeight / 2f,
                        viewWidth / 2f - mShadowRadius, paint);
            }

            private void updateRadialGradient(int diameter) {
                mRadialGradient = new RadialGradient(diameter / 2f, diameter / 2f,
                        mShadowRadius, new int[]{FILL_SHADOW_COLOR, Color.TRANSPARENT},
                        null, Shader.TileMode.CLAMP);
                mShadowPaint.setShader(mRadialGradient);
            }
        }
    }
}

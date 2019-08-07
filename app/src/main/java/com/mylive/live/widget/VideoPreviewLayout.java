package com.mylive.live.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

/**
 * Created by Developer Zailong Shi on 2019-07-19.
 */
public class VideoPreviewLayout extends FrameLayout {

    private GestureDetector gestureDetector;
    private View videoView, previewView;
    private boolean isOpened, isMoving, isLocked;
    private int duration = 200;
    private OnScrollListener onScrollListener;
    private OnLayoutListener onLayoutListener;
    private int currentX, minX, maxX;
    private int videoViewId, previewViewId;
    private float sensitivity = 0.5f;

    public VideoPreviewLayout(Context context) {
        super(context);
    }

    public VideoPreviewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoPreviewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setViewIds(videoViewId, previewViewId);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (h != oldh) {
            restoreState();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (onLayoutListener != null) {
            onLayoutListener.onLayout();
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle state = new Bundle();
        state.putParcelable("super_state", super.onSaveInstanceState());
        state.putBoolean("is_opened", isOpened);
        state.putFloat("sensitivity", sensitivity);
        state.putInt("duration", duration);
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Parcelable superState = ((Bundle) state).getParcelable("super_state");
            super.onRestoreInstanceState(superState);
            isOpened = ((Bundle) state).getBoolean("is_opened");
            sensitivity = ((Bundle) state).getFloat("sensitivity");
            duration = ((Bundle) state).getInt("duration");
            restoreState();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev) || (!isLocked && gestureDetector.onTouchEvent(ev));
    }

    {
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                resetParams();
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (isMoving) {
                    move((int) distanceX);
                    return true;
                }
                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    boolean opening = distanceX > 0;
                    if (opening != isOpened) {
                        if (getParent() != null) {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        isMoving = true;
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(velocityX) > Math.abs(velocityY)
                        && Math.abs(velocityX) > 100) {
                    setState((int) velocityX);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (super.onTouchEvent(event)) {
            return true;
        }
        boolean result = gestureDetector.onTouchEvent(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                performClick();
            case MotionEvent.ACTION_CANCEL:
                isMoving = false;
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                if (!result) {
                    setState(0);
                }
                break;
        }
        return result;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void startUpdateViewAnimation() {
        if (maxX == 0) {
            return;
        }
        int startX = currentX;
        int endX = isOpened ? minX : maxX;
        int duration = (int) (this.duration * ((float) Math.abs(startX - endX) / (maxX - minX)));
        ValueAnimator valueAnimator = ValueAnimator.ofInt(startX, endX);
        valueAnimator.addUpdateListener(animation -> {
            int x = (int) animation.getAnimatedValue();
            updateViewLocation(x);
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    private void move(int dx) {
        dx *= sensitivity;
        currentX = Math.max(minX, Math.min(currentX - dx, maxX));
        updateViewLocation(currentX);
    }

    private void setState(int vx) {
        if (vx != 0) {
            isOpened = vx < 0;
        } else {
            isOpened = currentX < (maxX - minX) / 2 + minX;
        }
        onStateChanged();
    }

    private void onStateChanged() {
        if (isInvalidLocation()) {
            startUpdateViewAnimation();
        }
        if (onScrollListener != null) {
            onScrollListener.onStateChanged(isOpened);
        }
    }

    private boolean isInvalidLocation() {
        return isOpened && currentX > minX
                || !isOpened && currentX < maxX;
    }

    private void resetParams() {
        if (previewView == null) {
            return;
        }
        isMoving = false;
        maxX = getWidth();
        minX = maxX - previewView.getWidth();
        currentX = isOpened ? minX : maxX;
    }

    private void restoreState() {
        resetParams();
        updateViewLocation(currentX);
    }

    private void updateViewLocation(int x) {
        if (maxX == 0) {
            return;
        }
        previewView.setX(x + (x == maxX || x == minX ? 0 : -1));//fix unknown bug
        float ratio = (float) x / maxX;
        int height = (int) Math.ceil((getHeight() - getPaddingTop() - getPaddingBottom()) / ratio);
        ViewGroup.LayoutParams params = videoView.getLayoutParams();
        params.height = height;
        videoView.setLayoutParams(params);
        int dx = getWidth() - (int) (maxX * ratio);
//            int dy = height - getHeight();
        videoView.setTranslationX(-dx >> 1);
//            videoView.setTranslationY(-dy >> 1);
        videoView.setTranslationY(x == maxX ? 0 : -1);//fix unknown bug
        videoView.setScaleX(ratio);
        videoView.setScaleY(ratio);
        if (onScrollListener != null) {
            onScrollListener.onScroll(x);
        }
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void toggle() {
        open(!isOpened);
    }

    public void open(boolean open) {
        if (isLocked) {
            return;
        }
        if (isOpened == open) {
            return;
        }
        isOpened = open;
        onStateChanged();
    }

    public void open() {
        open(true);
    }

    public void close() {
        open(false);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setOnScrollListener(OnScrollListener l) {
        this.onScrollListener = l;
    }

    public float getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }

    public void setViewIds(int videoViewId, int previewViewId) {
        this.videoViewId = videoViewId;
        this.previewViewId = previewViewId;
        videoView = findViewById(videoViewId);
        previewView = findViewById(previewViewId);
        restoreState();
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public void setOnLayoutListener(OnLayoutListener onLayoutListener) {
        this.onLayoutListener = onLayoutListener;
    }

    public interface OnScrollListener {
        void onStateChanged(boolean opened);

        void onScroll(int currentX);
    }

    public interface OnLayoutListener {
        void onLayout();
    }
}

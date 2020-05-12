package com.mylive.live.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import java.util.LinkedList;

/**
 * Created by Developer Zailong Shi on 2019/3/18.
 */
public class VerticalMarqueeView extends FrameLayout {

    private static final long DURATION = 1000, DELAY_TIME = 3000;
    private LinkedList<View> viewCaches;
    private Adapter adapter;
    private boolean isRunning, isPaused;
    private boolean isMeasured, startDelayed, restart;
    private int totalCount, currentIndex, dividerHeight;
    private long resumeDelayTime, startTimeOfAddTask, delayTimeOfAddTask;
    private ValueAnimator valueAnimator;
    private Runnable delayAddTask;

    {
        dividerHeight = (int) (3.5f * getResources().getDisplayMetrics().density);
    }

    public VerticalMarqueeView(Context context) {
        this(context, null);
    }

    public VerticalMarqueeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalMarqueeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        startDelay();
    }

    private void startDelay() {
        if (!isMeasured) {
            isMeasured = true;
        }
        if (startDelayed) {
            startDelayed = false;
            start();
        }
    }

    public void start() {
        if (adapter == null) {
            throw new IllegalStateException("Call start method must set the Adapter first.");
        }
        isRunning = true;
        isPaused = false;
        if (!isMeasured) {
            startDelayed = true;
            return;
        }
        currentIndex = -1;
        if (valueAnimator != null
                && valueAnimator.isRunning()) {
            restart = true;
            return;
        }
        restart = false;
        startAnimator();
    }

    public void resume() {
        if (!isPaused) {
            return;
        }
        isPaused = false;
        if (valueAnimator != null) {
            if (valueAnimator.isPaused()) {
                valueAnimator.resume();
            } else if (hasNextAnimator()) {
                setDelayTimeOfAddTask(resumeDelayTime);
                postDelayed(delayAddTask = this::startAnimator,
                        resumeDelayTime);
            }
        }
    }

    public void pause() {
        if (!isRunning) {
            return;
        }
        isPaused = true;
        if (valueAnimator != null) {
            if (valueAnimator.isRunning()) {
                valueAnimator.pause();
            } else {
                resetResumeDelayTime();
                removeCallbacks(delayAddTask);
            }
        }
    }

    public void stop() {
        isRunning = false;
        isPaused = false;
        clearOldAnimator();
        removeAndRecycleView();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isPaused() {
        return isPaused;
    }

    private void clearOldAnimator() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
            valueAnimator = null;
        }
        removeCallbacks(delayAddTask);
    }

    private void resetResumeDelayTime() {
        resumeDelayTime = delayTimeOfAddTask
                - System.currentTimeMillis()
                + startTimeOfAddTask;
    }

    private void setDelayTimeOfAddTask(long delay) {
        startTimeOfAddTask = System.currentTimeMillis();
        delayTimeOfAddTask = delay;
    }

    public <VH extends ViewHolder> VerticalMarqueeView setAdapter(Adapter<VH> adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException("Cannot set a null Adapter");
        }
        this.adapter = adapter;
        return this;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public Adapter getAdapter() {
        return adapter;
    }

    private void startAnimator() {
        if (isPaused) {
            resetResumeDelayTime();
            return;
        }
        View childEnter = getChildView();
        if (childEnter == null)
            return;
        addView(childEnter);
        View childExit = null;
        final int childCount = getChildCount(), indexOffset = 2;
        if (childCount >= indexOffset) {
            childExit = getChildAt(childCount - indexOffset);
        }
        measureChild(childEnter, getWidth(), getHeight());
        final View finalChildExit = childExit;
        final int parentHeight = getHeight();
        final int childEnterHeight = childEnter.getMeasuredHeight();
        final int childExitHeight = childExit == null ? 0 : childExit.getHeight();
        final int startY = parentHeight + dividerHeight;
        final int endY = parentHeight - childEnterHeight;
        childEnter.setY(startY);
        childEnter.setAlpha(1);
        valueAnimator = ValueAnimator.ofInt(startY, endY);
        valueAnimator.addUpdateListener(animation -> {
            if (childEnter.getParent() == null)
                return;
            final float fraction = animation.getAnimatedFraction();
            final int y = (int) animation.getAnimatedValue();
            childEnter.setY(y);
            if (finalChildExit != null) {
                finalChildExit.setY(parentHeight - childExitHeight
                        - fraction * (childExitHeight + dividerHeight));
                finalChildExit.setAlpha((1 - fraction) * 1);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (childEnter != null) {
                    LayoutParams params = (LayoutParams) childEnter.getLayoutParams();
                    params.viewHolder.onViewEntered();
                }
                if (finalChildExit != null) {
                    removeAndRecycleView(finalChildExit);
                }
                if (hasNextAnimator() || restart) {
                    final long finalDelay = restart ? 0 : DELAY_TIME;
                    setDelayTimeOfAddTask(finalDelay);
                    removeCallbacks(delayAddTask);
                    postDelayed(delayAddTask = () -> startAnimator(), finalDelay);
                }
                restart = false;
            }
        });
        valueAnimator.setDuration(DURATION);
        valueAnimator.start();
    }

    private boolean hasNextAnimator() {
        totalCount = adapter.getItemCount();
        return totalCount > 1;
    }

    private View getChildView() {
        //noinspection SynchronizeOnNonFinalField: fix out of rang array.
        synchronized (adapter) {
            totalCount = adapter.getItemCount();
            if (totalCount > 0) {
                currentIndex = (currentIndex + 1) % totalCount;
                View child = viewCaches != null && viewCaches.size() > 0 ?
                        viewCaches.removeFirst() : null;
                if (child == null) {
                    child = assemblyLayoutParams(adapter.onCreateViewHolder(this));
                }
                LayoutParams params = (LayoutParams) child.getLayoutParams();
                //noinspection unchecked
                adapter.onBindViewHolder(params.viewHolder, currentIndex);
                return child;
            }
        }
        return null;
    }

    private View assemblyLayoutParams(ViewHolder holder) {
        LayoutParams params;
        if (holder.itemView.getLayoutParams() == null) {
            params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
        } else if (holder.itemView.getLayoutParams() instanceof MarginLayoutParams) {
            params = new LayoutParams(
                    (MarginLayoutParams) holder.itemView.getLayoutParams());
        } else {
            params = new LayoutParams(holder.itemView.getLayoutParams());
        }
        params.viewHolder = holder;
        holder.itemView.setLayoutParams(params);
        return holder.itemView;
    }

    private void removeAndRecycleView() {
        int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View child = getChildAt(i);
            removeAndRecycleView(child);
        }
    }

    private void removeAndRecycleView(View child) {
        if (child == null)
            return;
        if (child.getParent() != null) {
            ((ViewGroup) child.getParent()).removeView(child);
        }
        if (child.getLayoutParams() instanceof LayoutParams) {
            ViewHolder holder = ((LayoutParams) child.getLayoutParams()).viewHolder;
            if (holder != null) {
                if (viewCaches == null) {
                    viewCaches = new LinkedList<>();
                }
                if (!viewCaches.contains(child)) {
                    viewCaches.add(child);
                }
            }
        }
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {

        private ViewHolder viewHolder;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

    }

    public static abstract class Adapter<VH extends ViewHolder> {

        public abstract int getItemCount();

        public abstract VH onCreateViewHolder(ViewGroup parent);

        public abstract void onBindViewHolder(VH holder, int position);
    }

    public static class ViewHolder {

        public final View itemView;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
        }

        public void onViewEntered() {

        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handleVisibilityEvent(false);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        handleVisibilityEvent(visibility == View.VISIBLE);
    }

    private void handleVisibilityEvent(boolean visible) {
        if (visible) {
            resume();
        } else {
            pause();
        }
    }
}

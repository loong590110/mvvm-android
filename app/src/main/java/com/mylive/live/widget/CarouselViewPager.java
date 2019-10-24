package com.mylive.live.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Developer Zailong Shi on 2019-10-21.
 */
public class CarouselViewPager extends ViewPager {

    private final int DEFAULT_ANIMATION_DURATION = 200;//ms
    private int animationDuration = DEFAULT_ANIMATION_DURATION;//ms
    private long interval = 5000;//ms
    private boolean playing, paused;
    private Handler handler;
    private Runnable runnable;
    private Interpolator interpolator;

    private final Interpolator DEFAULT_INTERPOLATOR = new Interpolator() {
        @Override
        public float getInterpolation(float input) {
            input -= 1.0f;
            return input * input * input * input * input + 1.0f;
        }
    };

    public CarouselViewPager(@NonNull Context context) {
        super(context);
    }

    public CarouselViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    {
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    pause();
                } else if (state == ViewPager.SCROLL_STATE_IDLE) {
                    resume();
                }
            }
        });
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Bundle state = new Bundle();
        state.putParcelable("superState", superState);
        state.putLong("interval", interval);
        state.putBoolean("playing", playing);
        state.putBoolean("paused", paused);
        return state;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        Bundle data = (Bundle) state;
        Parcelable superState = data.getParcelable("superState");
        super.onRestoreInstanceState(superState);
        interval = data.getLong("interval");
        playing = data.getBoolean("playing");
        paused = data.getBoolean("paused");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        resume();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            resume();
        } else {
            pause();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        pause();
    }

    public CarouselViewPager setAnimationDuration(int duration) {
        return setAnimationDuration(duration, null);
    }

    public CarouselViewPager setAnimationDuration(int duration, Interpolator interpolator) {
        try {
            animationDuration = duration;
            this.interpolator = interpolator == null ? DEFAULT_INTERPOLATOR : interpolator;
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            Scroller mScroller = new Scroller(getContext(), this.interpolator) {

                @Override
                public void startScroll(int startX, int startY, int dx, int dy, int duration) {
                    duration = animationDuration;
                    super.startScroll(startX, startY, dx, dy, duration);
                }

                @Override
                public void startScroll(int startX, int startY, int dx, int dy) {
                    super.startScroll(startX, startY, dx, dy, animationDuration);
                }
            };
            mField.set(this, mScroller);
        } catch (NoSuchFieldException | IllegalArgumentException |
                IllegalAccessException ignore) {
            animationDuration = DEFAULT_ANIMATION_DURATION;
        }
        return this;
    }

    public CarouselViewPager setInterval(long interval) {
        this.interval = interval;
        return this;
    }

    public void play() {
        stop();
        playing = true;
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                if (!playing || paused) {
                    return;
                }
                if (getAdapter() == null) {
                    throw new IllegalStateException("unset adapter");
                }
                if (getAdapter().getCount() < 2) {
                    return;
                }
                int nextItem = getCurrentItem() + 1;
                int item = nextItem % getAdapter().getCount();
                boolean diff = item != nextItem;
                setCurrentItem(item, !diff);
                if (diff) {
                    if (getAdapter() instanceof Adapter) {
                        Adapter adapter = (Adapter) getAdapter();
                        View firstChild = adapter.getItemView(0);
                        View lastChild = adapter.getItemView(adapter.getCount() - 1);
                        if (firstChild != null && lastChild != null) {
                            TranslateAnimation animation = new TranslateAnimation(
                                    Animation.RELATIVE_TO_SELF, 1f,
                                    Animation.RELATIVE_TO_SELF, 0f,
                                    Animation.RELATIVE_TO_SELF, 0f,
                                    Animation.RELATIVE_TO_SELF, 0f
                            );
                            animation.setInterpolator(
                                    interpolator == null ? DEFAULT_INTERPOLATOR : interpolator
                            );
                            animation.setDuration(animationDuration);
                            lastChild.layout(
                                    getScrollX() - lastChild.getWidth(),
                                    0, 0, lastChild.getHeight()
                            );
                            firstChild.startAnimation(animation);
                            lastChild.startAnimation(animation);
                        }
                    }
                }
                handler.postDelayed(this, interval);
            }
        }, interval);
    }

    public void resume() {
        if (playing && paused) {
            paused = false;
            play();
        }
    }

    public void pause() {
        if (playing) {
            stop();
            playing = true;
            paused = true;
        }
    }

    public void stop() {
        playing = false;
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    public abstract static class ViewHolder {
        public View itemView;

        public ViewHolder(View itemView) {
            Objects.requireNonNull(itemView);
            this.itemView = itemView;
        }
    }

    public abstract static class Adapter<VH extends ViewHolder> extends PagerAdapter {

        private SparseArray<VH> viewHolders = new SparseArray<>();
        private List<VH> viewHolderCache = new ArrayList<>();

        public View getItemView(int position) {
            if (viewHolders.get(position) != null) {
                return viewHolders.get(position).itemView;
            }
            return null;
        }

        @Override
        public final boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public final Object instantiateItem(@NonNull ViewGroup container, int position) {
            VH viewHolder = viewHolders.get(position);
            if (viewHolder == null) {
                viewHolder = viewHolderCache.size() > 0 ? viewHolderCache.remove(0) : null;
                if (viewHolder == null) {
                    viewHolder = onCreateViewHolder(container.getContext());
                    if (viewHolder == null) {
                        throw new NullPointerException();
                    }
                }
            }
            onBindViewHolder(viewHolder, position);
            if (viewHolder.itemView.getParent() == null) {
                container.addView(viewHolder.itemView);
                viewHolders.put(position, viewHolder);
            }
            return viewHolder.itemView;
        }

        @Override
        public final void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            if (position == getCount() - 1) {
                return;
            }
            container.removeView((View) object);
            viewHolderCache.add(viewHolders.get(position));
            viewHolders.delete(position);
        }

        protected abstract VH onCreateViewHolder(Context context);

        protected abstract void onBindViewHolder(VH holder, int position);
    }
}
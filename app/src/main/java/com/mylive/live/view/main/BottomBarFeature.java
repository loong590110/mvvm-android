package com.mylive.live.view.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.mylive.live.R;
import com.mylive.live.arch.annotation.FieldMap;
import com.mylive.live.arch.feature.FeaturesActivity;
import com.mylive.live.arch.feature.FeaturesFragment;
import com.mylive.live.arch.feature.FeaturesManagerOwner;
import com.mylive.live.base.BaseFeature;
import com.mylive.live.utils.ScrollEvent;
import com.mylive.live.databinding.ActivityMainBinding;
import com.mylive.live.view.home.HomeScrollEvent;

/**
 * Create by zailongshi on 2019/7/8
 */
public class BottomBarFeature extends BaseFeature {

    @FieldMap("binding")
    private ActivityMainBinding binding;
    private ScrollEvent.Observer scrollEventObserver;

    public BottomBarFeature(FeaturesManagerOwner owner) {
        super(owner);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onCreate() {
        HomeScrollEvent.getInstance().registerObserver(
                scrollEventObserver = direction -> {
                    int parentHeight = binding.getRoot().getHeight();
                    //最大可见高度为中间tab的高度（也是整体高度）
                    int bottomBarMaxHeight = binding.tabBar.getHeight();
                    //最小可见高度为其余tab的高度（正好是背景高度）
                    int bottomBarMinHeight = binding.tabBar
                            .findViewById(R.id.background).getHeight();
                    int startY = direction == ScrollEvent.Direction.UP ?
                            parentHeight - bottomBarMaxHeight
                            : parentHeight;
                    int endY = direction == ScrollEvent.Direction.UP ? parentHeight
                            : parentHeight - bottomBarMaxHeight;
                    Runnable resetRecyclerViewLayoutParams = () -> {
                        ViewGroup.MarginLayoutParams params
                                = (ViewGroup.MarginLayoutParams) binding.fragmentHost.getLayoutParams();
                        params.bottomMargin = direction == ScrollEvent.Direction.UP ?
                                bottomBarMinHeight : 0;
                        binding.fragmentHost.setLayoutParams(params);
                    };
                    resetRecyclerViewLayoutParams.run();
                    ValueAnimator valueAnimator = ValueAnimator.ofInt(startY, endY);
                    valueAnimator.addUpdateListener(animation -> {
                        binding.tabBar.setY((int) animation.getAnimatedValue());
                        Runnable resetRecyclerViewLayoutParams2 = () -> {
                            int marginBottom = bottomBarMinHeight - (int) animation.getAnimatedValue();
                            ViewGroup.LayoutParams params = binding.fragmentHost.getLayoutParams();
                            ((ViewGroup.MarginLayoutParams) params).bottomMargin
                                    = Math.min(bottomBarMinHeight, Math.max(0, marginBottom));
                            binding.fragmentHost.setLayoutParams(params);
                        };
                        resetRecyclerViewLayoutParams2.run();
                    });
                    valueAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            HomeScrollEvent.getInstance().onFeedBack(direction);
                        }
                    });
                    valueAnimator.setInterpolator(new DecelerateInterpolator());
                    valueAnimator.setDuration(350);
                    valueAnimator.start();
                }
        );
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        HomeScrollEvent.getInstance().unregisterObserver(scrollEventObserver);
    }
}

package com.mylive.live.view.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import com.mylive.live.arch.annotation.FieldMap;
import com.mylive.live.arch.feature.FeaturesManagerOwner;
import com.mylive.live.base.BaseFeature;
import com.mylive.live.databinding.ActivityMainBinding;
import com.mylive.live.utils.ScrollEvent;
import com.mylive.live.view.home.HomeScrollEvent;
import com.mylive.live.widget.TabHost;

/**
 * Create by zailongshi on 2019/7/8
 */
public class BottomBarFeature extends BaseFeature {

    @FieldMap("binding")
    private ActivityMainBinding binding;
    @FieldMap("tabHost")
    private TabHost tabHost;
    private ScrollEvent.Observer scrollEventObserver;

    public BottomBarFeature(FeaturesManagerOwner owner) {
        super(owner);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onCreate() {
        HomeScrollEvent.getInstance().filter(() ->
                tabHost.getSelectedIndex() == 0
        ).registerObserver(scrollEventObserver =
                direction -> {
                    int parentHeight = binding.getRoot().getHeight();
                    int bottomBarHeight = binding.tabBar.getRoot().getHeight();
                    int startY = direction == ScrollEvent.Direction.UP ?
                            parentHeight - bottomBarHeight
                            : parentHeight;
                    int endY = direction == ScrollEvent.Direction.UP ? parentHeight
                            : parentHeight - bottomBarHeight;
                    ValueAnimator valueAnimator = ValueAnimator.ofInt(startY, endY);
                    valueAnimator.addUpdateListener(animation -> {
                        binding.tabBar.getRoot().setY((int) animation.getAnimatedValue());
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

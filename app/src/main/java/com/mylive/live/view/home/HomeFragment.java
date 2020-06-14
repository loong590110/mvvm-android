package com.mylive.live.view.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.mylive.live.arch.annotation.FieldMap;
import com.mylive.live.arch.subscriber.Scheduler;
import com.mylive.live.arch.theme.StatusBarCompat;
import com.mylive.live.base.BaseFragment;
import com.mylive.live.databinding.FragmentHomeBinding;
import com.mylive.live.model.HttpResp;
import com.mylive.live.router.LiveRoomActivityStarter;

/**
 * Created by Developer Zailong Shi on 2019-06-28.
 */
public class HomeFragment extends BaseFragment {

    @FieldMap("binding")
    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        StatusBarCompat.getSettings(getActivity()).setLightMode(true).apply();
        return (binding = FragmentHomeBinding.inflate(
                inflater, container, false
        )).getRoot();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        StatusBarCompat.getSettings(getActivity()).setLightMode(!hidden).apply();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((ViewGroup) view).setClipChildren(false);
        binding.edtSearch.clearFocus();
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            private final String[] tabs = {"首页", "动画", "番剧", "国创", "音乐", "电视剧", "纪录片", "科技", "游戏", "生活", "娱乐"};

            @NonNull
            @Override
            public Fragment getItem(int position) {
                return HomeTabFragment.newInstance(position);
            }

            @Override
            public int getCount() {
                return tabs.length;
            }

            @NonNull
            @Override
            public CharSequence getPageTitle(int position) {
                return tabs[position];
            }
        });
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1, 1.5f, 1, 1.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f
        );
        scaleAnimation.setRepeatCount(5);
        scaleAnimation.setDuration(300);
        binding.btnNew.setOnClickListener(v -> {
            //v.startAnimation(scaleAnimation);
            LiveRoomActivityStarter.create().start(this);
        });
    }

    @Override
    protected void onSubscribe(Scheduler scheduler) {
        super.onSubscribe(scheduler);
        scheduler.subscribe(HttpResp.class, httpResp -> {

        });
    }
}

package com.mylive.live.view.main;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mylive.live.R;
import com.mylive.live.arch.annotation.Features;
import com.mylive.live.arch.annotation.FieldMap;
import com.mylive.live.arch.subscriber.Scheduler;
import com.mylive.live.arch.theme.StatusBarCompat;
import com.mylive.live.base.BaseActivity;
import com.mylive.live.databinding.ActivityMainBinding;
import com.mylive.live.event.TestEvent;
import com.mylive.live.utils.DoubleClickExit;
import com.mylive.live.utils.ToastUtils;
import com.mylive.live.view.home.HomeFragment;
import com.mylive.live.widget.FragmentHost;
import com.mylive.live.widget.TabHost;

/**
 * Created by Developer Zailong Shi on 2019-06-19.
 */
@Features({BottomBarFeature.class})
public class MainActivity extends BaseActivity {

    @FieldMap("binding")
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.getSettings(this).setLightMode(true).apply();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        TabHost.create()
                .setTabs(binding.tabBar.findViewById(R.id.tab_home),
                        binding.tabBar.findViewById(R.id.tab_channel),
                        binding.tabBar.findViewById(R.id.tab_news),
                        binding.tabBar.findViewById(R.id.tab_mine))
                .setOnTabSelectedListener(new TabHost.OnTabSelectedListener() {
                    private FragmentHost fragmentHost = FragmentHost.create(
                            getSupportFragmentManager(),
                            R.id.fragment_host
                    );

                    @Override
                    public void onTabSelected(View tab) {
                        tab.setSelected(true);
                        Class<? extends Fragment> fragment = HomeFragment.class;
                        switch (tab.getId()) {
                            case R.id.tab_channel:
                                break;
                            case R.id.tab_news:
                                break;
                            case R.id.tab_mine:
                                break;
                        }
                        fragmentHost.show(tab.getContext(), fragment);
                    }

                    @Override
                    public void onTabUnselected(View tab) {
                        tab.setSelected(false);
                    }
                })
                .select(0);
    }

    @Override
    protected void onSubscribe(Scheduler scheduler) {
        super.onSubscribe(scheduler);
        scheduler.subscribe(String.class, event -> {
            ToastUtils.showShortToast(this, event);
        }).subscribe(Integer.class, event -> {
            ToastUtils.showShortToast(this, "" + event);
        }).subscribe(TestEvent.class, event -> {
            ToastUtils.showShortToast(this, event.data);
        });
    }

    @Override
    public void onBackPressed() {
        if (!DoubleClickExit.getInstance().onBackPressed()) {
            ToastUtils.showShortToast(this, R.string.double_click_exit_app);
            return;
        }
        super.onBackPressed();
    }
}

package com.mylive.live.view.main;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
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
import com.mylive.live.view.channel.ChannelFragment;
import com.mylive.live.view.home.HomeFragment;
import com.mylive.live.view.mine.MineFragment;
import com.mylive.live.view.news.NewsFragment;
import com.mylive.live.widget.TabHost;

/**
 * Created by Developer Zailong Shi on 2019-06-19.
 */
@Features({BottomBarFeature.class})
public class MainActivity extends BaseActivity {

    @FieldMap("binding")
    private ActivityMainBinding binding;
    @FieldMap("tabHost")
    private TabHost tabHost;

    private void onThemeUpdate() {
        StatusBarCompat.getSettings(this)
                .setImmersive(true)
                .apply();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        onThemeUpdate();
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        tabHost = TabHost.create()
                .setAdapter(new TabHost.FragmentAdapter(getSupportFragmentManager(),
                        R.id.fragment_host) {
                    @Override
                    public Fragment getFragment(View tab) {
                        Fragment fragment = null;
                        switch (tab.getId()) {
                            case R.id.tab_home:
                                fragment = Fragment.instantiate(
                                        MainActivity.this,
                                        HomeFragment.class.getName());
                                break;
                            case R.id.tab_channel:
                                fragment = Fragment.instantiate(
                                        MainActivity.this,
                                        ChannelFragment.class.getName());
                                break;
                            case R.id.tab_news:
                                fragment = Fragment.instantiate(
                                        MainActivity.this,
                                        NewsFragment.class.getName());
                                break;
                            case R.id.tab_mine:
                                fragment = Fragment.instantiate(
                                        MainActivity.this,
                                        MineFragment.class.getName());
                                break;
                        }
                        return fragment;
                    }
                })
                .setTabs(binding.tabBar.tabHome,
                        binding.tabBar.tabChannel,
                        binding.tabBar.tabNews,
                        binding.tabBar.tabMine)
                .setOnTabSelectedListener(new TabHost.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(View tab) {
                    }

                    @Override
                    public void onTabUnselected(View tab) {
                    }
                });
        tabHost.select(0);
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

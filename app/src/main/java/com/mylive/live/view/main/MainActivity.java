package com.mylive.live.view.main;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;

import com.mylive.live.R;
import com.mylive.live.arch.annotation.Features;
import com.mylive.live.arch.annotation.FieldMap;
import com.mylive.live.arch.subscriber.Scheduler;
import com.mylive.live.arch.theme.StatusBarCompat;
import com.mylive.live.base.BaseActivity;
import com.mylive.live.utils.DoubleClickExit;
import com.mylive.live.databinding.ActivityMainBinding;
import com.mylive.live.event.TestEvent;
import com.mylive.live.router.LiveRoomActivityStarter;
import com.mylive.live.utils.ToastUtils;

import java.io.IOException;

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
        try {
            String html = getAssets().open("about.html").toString();
            binding.txtHtml.setText(Html.fromHtml(html));
        } catch (IOException e) {
            e.printStackTrace();
        }
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

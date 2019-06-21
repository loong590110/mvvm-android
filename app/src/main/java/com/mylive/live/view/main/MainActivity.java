package com.mylive.live.view.main;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.mylive.live.R;
import com.mylive.live.core.base.BaseActivity;
import com.mylive.live.core.subscriber.Scheduler;
import com.mylive.live.databinding.ActivityMainBinding;
import com.mylive.live.event.TestEvent;
import com.mylive.live.router.LiveRoomActivityStarter;
import com.mylive.live.utils.ToastUtils;

/**
 * Created by Developer Zailong Shi on 2019-06-19.
 */
public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.btnJump.setOnClickListener(v -> {
            new LiveRoomActivityStarter().start(this);
        });
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
}

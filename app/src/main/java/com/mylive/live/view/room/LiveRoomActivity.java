package com.mylive.live.view.room;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.mylive.live.R;
import com.mylive.live.arch.mvvm.BaseActivity;
import com.mylive.live.arch.theme.StatusBarCompat;
import com.mylive.live.databinding.ActivityLiveRoomBinding;
import com.mylive.live.event.TestEvent;
import com.mylive.live.viewmodel.LiveRoomViewModel;

/**
 * Created by Developer Zailong Shi on 2019-06-21.
 */
public class LiveRoomActivity extends BaseActivity {

    private ActivityLiveRoomBinding binding;
    private LiveRoomViewModel liveRoomViewModel;
    private int msg = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBarCompat.getSettings(this)
                .setLightMode(true)
                .setImmersive(true)
                .apply();
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_live_room);
        liveRoomViewModel = ViewModelProviders.of(this).get(LiveRoomViewModel.class);
        binding.btnSend.setOnClickListener(v -> {
            publish(msg++ % 2 == 0 ? "hello" : msg);
            if (msg % 3 == 0) {
                publish(new TestEvent("test"));
            }
            StatusBarCompat.getSettings(this)
                    .setLightMode(msg % 2 == 0)
                    .setImmersive(msg % 2 == 0)
                    .apply();
        });
    }
}

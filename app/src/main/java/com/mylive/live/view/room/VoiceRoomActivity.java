package com.mylive.live.view.room;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.mylive.live.R;
import com.mylive.live.arch.theme.StatusBarCompat;
import com.mylive.live.base.BaseActivity;
import com.mylive.live.databinding.ActivityVoiceRoomBinding;
import com.mylive.live.event.TestEvent;
import com.mylive.live.router.LoginActivityStarter;
import com.mylive.live.viewmodel.LiveRoomViewModel;

/**
 * Created by Developer Zailong Shi on 2019-06-21.
 */
public class VoiceRoomActivity extends BaseActivity {

    private ActivityVoiceRoomBinding binding;
    private LiveRoomViewModel liveRoomViewModel;
    private int msg = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBarCompat.getSettings(this)
                .setLightMode(true)
                .setImmersive(true)
                .apply();
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_voice_room);
        liveRoomViewModel = ViewModelProviders.of(this).get(LiveRoomViewModel.class);
        binding.btnLogin.setOnClickListener(v -> {
            LoginActivityStarter.create().start(this);
        });
        binding.btnSend.setOnClickListener(v -> {
            publish(msg++ % 2 == 0 ? "hello" : msg);
            if (msg % 3 == 0) {
                publish(new TestEvent("test"));
            }
            StatusBarCompat.getSettings(this)
                    .setLightMode(msg % 2 == 0)
                    .setImmersive(msg % 2 == 0)
                    .apply();
            liveRoomViewModel.getConfig().observe(this, config -> {
                binding.txtStatue.setText(String.valueOf(config));
            });
        });
        binding.btnOpen.setOnClickListener(v -> {
            new GiftsDialogFragment.Builder().build().show(
                    getSupportFragmentManager(),
                    "dialog_gift"
            );
        });
    }
}

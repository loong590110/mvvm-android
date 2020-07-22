package com.mylive.live.view.room;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.mylive.live.R;
import com.mylive.live.arch.theme.StatusBarCompat;
import com.mylive.live.base.BaseActivity;
import com.mylive.live.databinding.ActivityVoiceRoomBinding;
import com.mylive.live.event.TestEvent;
import com.mylive.live.view.room.voicetrajectorygifts.VoiceTrajectoryGiftsDirector;
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
        VoiceTrajectoryGiftsDirector.INSTANCE.direct();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_voice_room);
        liveRoomViewModel = ViewModelProviders.of(this).get(LiveRoomViewModel.class);
        Glide.with(this).load("https://misc.aotu.io/ONE-SUNDAY/SteamEngine.png")
                .into(binding.imgCover);
        binding.btnSend.setOnClickListener(v -> {
            publish(msg++ % 2 == 0 ? "hello" : msg);
            if (msg % 3 == 0) {
                publish(new TestEvent("test"));
            }
//            StatusBarCompat.getSettings(this)
//                    .setLightMode(msg % 2 == 0)
//                    .setImmersive(msg % 2 == 0)
//                    .apply();
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

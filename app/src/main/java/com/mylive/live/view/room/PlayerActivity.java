package com.mylive.live.view.room;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.mylive.live.R;
import com.mylive.live.arch.theme.StatusBarCompat;
import com.mylive.live.base.BaseActivity;
import com.mylive.live.databinding.ActivityPlayerBinding;

public class PlayerActivity extends BaseActivity {
    private ActivityPlayerBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBarCompat.getSettings(this).setImmersive(true).apply();
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player);
        binding.videoView.setVideoPath("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
        binding.videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            binding.loadingView.hide();
        });
        binding.videoView.start();
        binding.loadingView.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.colorPrimary),
                PorterDuff.Mode.MULTIPLY
        );
        binding.loadingView.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.videoView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.videoView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.videoView.stopPlayback();
        binding.videoView.suspend();
    }
}

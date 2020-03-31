package com.mylive.live.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoView2 extends TextureView implements MediaController.MediaPlayerControl {
    private VideoView videoView;

    public VideoView2(Context context) {
        super(context);
    }

    public VideoView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(int pos) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}

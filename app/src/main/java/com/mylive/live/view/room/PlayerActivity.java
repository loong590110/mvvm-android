package com.mylive.live.view.room;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Choreographer;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.mylive.live.R;
import com.mylive.live.arch.theme.StatusBarCompat;
import com.mylive.live.arch.workflow.BackgroundWorker;
import com.mylive.live.arch.workflow.IoWorker;
import com.mylive.live.arch.workflow.WorkFlow;
import com.mylive.live.base.BaseActivity;
import com.mylive.live.databinding.ActivityPlayerBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class PlayerActivity extends BaseActivity {
    private static final int CAPTURE_INTERVAL = 30 * 1000;
    private static final String VIDEO_URL = "http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8";
//    private static final String VIDEO_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

    private ActivityPlayerBinding binding;
    private File saveVideoFramePath;
    private boolean open = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBarCompat.getSettings(this).setImmersive(true).apply();
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player);
        binding.videoView.setVideoPath(VIDEO_URL);
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
        saveVideoFramePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.videoView.resume();
        postFrameCallback(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.videoView.pause();
        removeFrameCallback();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.videoView.stopPlayback();
        binding.videoView.suspend();
    }

    private void postFrameCallback(boolean delay) {
        if (delay) {
            Choreographer.getInstance().postFrameCallbackDelayed(frameCallback, CAPTURE_INTERVAL);
        } else {
            Choreographer.getInstance().postFrameCallback(frameCallback);
        }
    }

    private void removeFrameCallback() {
        Choreographer.getInstance().removeFrameCallback(frameCallback);
    }

    /**
     * 获取是缩略图的方法
     *
     * @param url
     * @param width
     * @param height
     * @return
     */
    private Bitmap createVideoThumbnail(String url, int width, int height) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }
        if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

    private Choreographer.FrameCallback frameCallback = frameTimeNanos -> {
        if (isFinishing() || !open) {
            return;
        }
        if (binding.videoView.isPlaying()) {
//            Bitmap bitmap = createVideoThumbnail(VIDEO_URL, binding.videoView.getWidth(), binding.videoView.getHeight());
            Bitmap bitmap = binding.videoView.getBitmap();
            if (bitmap != null) {
                WorkFlow.begin(bitmap)
                        .deliver(BackgroundWorker.work(bm -> {
                            ByteArrayOutputStream os = new ByteArrayOutputStream();
                            int quality = 100;
                            do {
                                os.reset();
                                bitmap.compress(Bitmap.CompressFormat.WEBP, quality, os);
                                quality -= 10;
                            } while (os.size() >= 200 * 1024);
                            return os;
                        }))
                        .deliver(IoWorker.work(os -> {
                            FileOutputStream fos = null;
                            try {
                                String fileName = "video_frame_" + System.currentTimeMillis() + ".webp";
                                fos = new FileOutputStream(new File(saveVideoFramePath, fileName));
                                fos.write(os.toByteArray(), 0, os.size());
                                fos.flush();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                if (fos != null) {
                                    try {
                                        fos.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            return null;
                        }))
                        .end();
            }
        }
        postFrameCallback(true);
    };
}

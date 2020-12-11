package com.mylive.live.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class LessAnimationDrawable extends AnimationDrawable {
    private final SparseArray<AnnulusDrawable> drawables;
    private final ImageLoader loader;
    private final Callback callback;
    private final AtomicBoolean paused = new AtomicBoolean();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Callback callbackProxy = new Callback() {
        @Override
        public void call(int index, Drawable drawable) {
            compensate = SystemClock.uptimeMillis() - compensate;
            if (Thread.currentThread() == handler.getLooper().getThread()) {
                callback.call(index, drawable);
            } else {
                handler.post(() -> callback.call(index, drawable));
            }
        }
    };

    private long compensate;

    public static LessAnimationDrawable create(int frameCount, int duration, ImageLoader loader) {
        return new LessAnimationDrawable(frameCount, duration, loader);
    }

    private LessAnimationDrawable(int frameCount, int duration, ImageLoader loader) {
        Objects.requireNonNull(loader);
        if (frameCount <= 0) {
            throw new IllegalArgumentException("frame count <= 0");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("duration <= 0(ms)");
        }
        this.loader = loader;
        drawables = new SparseArray<>();
        callback = (index, drawable) -> {
            drawables.get(index).setDrawable(drawable);
            if (paused.get()) {
                paused.set(false);
                run();
            } else {
                start();
            }
        };
        for (int i = 0; i < frameCount; i++) {
            drawables.put(i, AnnulusDrawable.create(i));
            if (i > 0) {
                drawables.get(i).setPrevious(drawables.get(i - 1));
                drawables.get(i - 1).setNext(drawables.get(i));
                if (i == frameCount - 1) {
                    drawables.get(0).setPrevious(drawables.get(i));
                    drawables.get(i).setNext(drawables.get(0));
                }
            } else {
                drawables.get(i).setPrevious(drawables.get(i));
                drawables.get(i).setNext(drawables.get(i));
            }
            super.addFrame(drawables.get(i), duration);
        }
    }

    private void load(int index) {
        compensate = SystemClock.uptimeMillis();
        loader.load(index, callbackProxy);
    }

    @Override
    @Deprecated
    public final void addFrame(@NonNull Drawable frame, int duration) {
        throw new IllegalArgumentException("This method had been deprecated.");
    }

    @Override
    public void run() {
        if (drawables.size() >= 3) {
            if (getCurrent() instanceof AnnulusDrawable) {
                AnnulusDrawable drawable = (AnnulusDrawable) getCurrent();
                drawable.getPrevious().getPrevious().setDrawable(null);
            }
        }
        if (getCurrent() instanceof AnnulusDrawable) {
            AnnulusDrawable drawable = ((AnnulusDrawable) getCurrent()).getNext();
            if (drawable.getDrawable() == null) {
                paused.set(true);
                load(drawable.index);
                return;
            }
        }
        super.run();
    }

    @Override
    public void scheduleSelf(@NonNull Runnable what, long when) {
        super.scheduleSelf(what, when - compensate);
    }

    @Override
    public void start() {
        if (drawables.size() > 0 && drawables.get(0).getDrawable() == null) {
            load(0);
            return;
        }
        super.start();
    }

    private static class AnnulusDrawable extends Drawable {
        private final int index;
        private AnnulusDrawable previous;
        private AnnulusDrawable next;
        private Drawable drawable;
        private int alpha = -1;
        private ColorFilter colorFilter;

        public static AnnulusDrawable create(int index) {
            return new AnnulusDrawable(index);
        }

        private AnnulusDrawable(int index) {
            this.index = index;
        }

        @Override
        public int getIntrinsicWidth() {
            Drawable d = getDrawable();
            if (d != null) {
                return d.getIntrinsicWidth();
            }
            return super.getIntrinsicWidth();
        }

        @Override
        public int getIntrinsicHeight() {
            Drawable d = getDrawable();
            if (d != null) {
                return d.getIntrinsicHeight();
            }
            return super.getIntrinsicHeight();
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            Drawable d = getDrawable();
            if (d != null) {
                if (alpha != -1) {
                    d.setAlpha(alpha);
                }
                if (colorFilter != null) {
                    d.setColorFilter(colorFilter);
                }
                d.setBounds(getBounds());
                d.draw(canvas);
            }
        }

        @Override
        public void setAlpha(int alpha) {
            this.alpha = alpha;
            Drawable d = getDrawable();
            if (d != null) {
                d.setAlpha(alpha);
                invalidateSelf();
            }
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            this.colorFilter = colorFilter;
            Drawable d = getDrawable();
            if (d != null) {
                d.setColorFilter(colorFilter);
                invalidateSelf();
            }
        }

        @Override
        public int getOpacity() {
            Drawable d = getDrawable();
            if (d != null) {
                return d.getOpacity();
            }
            return PixelFormat.TRANSPARENT;
        }

        public Drawable getDrawable() {
            return drawable;
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
            invalidateSelf();
        }

        public AnnulusDrawable getPrevious() {
            return previous;
        }

        public void setPrevious(AnnulusDrawable previous) {
            this.previous = previous;
        }

        public AnnulusDrawable getNext() {
            return next;
        }

        public void setNext(AnnulusDrawable next) {
            this.next = next;
        }
    }

    public interface ImageLoader {
        void load(int index, Callback callback);
    }

    public interface Callback {
        void call(int index, Drawable drawable);
    }
}

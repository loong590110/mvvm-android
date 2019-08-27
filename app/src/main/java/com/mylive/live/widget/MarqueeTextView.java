package com.mylive.live.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.View;
import android.view.ViewGroup;

import com.mylive.live.R;

/**
 * copyright(c) https://github.com/385841539/MarqueeView
 *
 * Created by Developer Zailong Shi on 2019-07-30.
 */
public class MarqueeTextView extends View {
    private static final String TAG = MarqueeTextView.class.getSimpleName();
    private float speed;
    private int textColor;
    private float textSize;
    private int textDistance;
    private int textDistancePx;
    private int repeatType;
    public static final int REPEAT_ONCETIME = 0;
    public static final int REPEAT_INTERVAL = 1;
    public static final int REPEAT_CONTINUOUS = 2;
    private float startLocation;
    private boolean isClickStop;
    private boolean isResetLocation;
    private float xLocation;
    private int contentWidth;
    private boolean isRoll;
    private TextPaint paint;
    private Rect rect;
    private boolean resetInit;
    private String text;
    private float textHeight;
    private long lastFrameTimeNanos;

    public MarqueeTextView(Context context) {
        this(context, null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.speed = 1.0F;
        this.textColor = Color.DKGRAY;
        this.textSize = 12.0F;
        this.textDistance = 10;
        this.repeatType = REPEAT_CONTINUOUS;
        this.startLocation = .0F;
        this.isClickStop = false;
        this.isResetLocation = true;
        this.xLocation = 0.0F;
        this.isRoll = false;
        this.resetInit = true;
        this.text = "";
        this.initAttrs(attrs);
        this.initPaint();
        this.initClick();
        this.textDistancePx = (int) (this.textDistance * getTextWidth("_"));
        this.contentWidth = (int) this.getTextWidth(this.text);
    }

    private void initClick() {
        this.setOnClickListener(v -> {
            if (isClickStop) {
                if (isRoll) {
                    stopRoll();
                } else {
                    continueRoll();
                }
            }
        });
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray tta = getContext().obtainStyledAttributes(attrs, R.styleable.MarqueeTextView);
        this.text = tta.getString(R.styleable.MarqueeTextView_marqueeText);
        this.textColor = tta.getColor(R.styleable.MarqueeTextView_marqueeTextColor,
                this.textColor);
        this.textSize = tta.getFloat(R.styleable.MarqueeTextView_marqueeTextSize,
                this.textSize);
        this.isClickStop = tta.getBoolean(R.styleable.MarqueeTextView_isClickableStop,
                this.isClickStop);
        this.isResetLocation = tta.getBoolean(R.styleable.MarqueeTextView_isResetLocation,
                this.isResetLocation);
        this.speed = tta.getFloat(R.styleable.MarqueeTextView_textSpeed, this.speed);
        this.textDistance = tta.getInteger(R.styleable.MarqueeTextView_textDistance,
                this.textDistance);
        this.startLocation = tta.getFloat(R.styleable.MarqueeTextView_textStartLocation,
                this.startLocation);
        this.repeatType = tta.getInt(R.styleable.MarqueeTextView_repeatType,
                this.repeatType);
        tta.recycle();
    }

    private void initPaint() {
        this.rect = new Rect();
        this.paint = new TextPaint(1);
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setColor(this.textColor);
        this.paint.setTextSize((float) this.dp2px(this.textSize));
    }

    public int dp2px(float dpValue) {
        float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5F);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    (int) getTextHeight(),
                    MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.resetInit) {
            this.setTextDistance(this.textDistance);
            if (this.startLocation < 0.0F) {
                this.startLocation = 0.0F;
            } else if (this.startLocation > 1.0F) {
                this.startLocation = 1.0F;
            }

            this.xLocation = (float) this.getWidth() * this.startLocation;
            this.resetInit = false;
        } else {
            long frameTimeNanos = System.nanoTime();
            if (lastFrameTimeNanos <= 0) {
                lastFrameTimeNanos = frameTimeNanos;
            }
            float frameDuration = 1000 * 1000 * 1000 / (2 * 60f);//120fps
            float times = ((frameTimeNanos - lastFrameTimeNanos) / frameDuration);
            lastFrameTimeNanos = frameTimeNanos;
            xLocation -= speed * times;
        }
        sendMarqueeMessage();

        switch (this.repeatType) {
            case REPEAT_ONCETIME:
                if ((float) this.contentWidth < -this.xLocation) {
                    this.stopRoll();
                }
                break;
            case REPEAT_INTERVAL:
                if ((float) this.contentWidth <= -this.xLocation) {
                    this.xLocation = (float) this.getWidth();
                }
                break;
            case REPEAT_CONTINUOUS:
                if ((float) this.contentWidth <= -this.xLocation) {
                    this.xLocation = this.textDistancePx;
                }
                break;
            default:
                if ((float) this.contentWidth < -this.xLocation) {
                    this.stopRoll();
                }
        }

        if (!TextUtils.isEmpty(this.text)) {
            canvas.drawText(this.text, this.xLocation,
                    (float) (this.getMeasuredHeight() / 2) + this.textHeight / 2.0F,
                    this.paint);
            if (this.repeatType == REPEAT_CONTINUOUS) {
                float repeatXLocation = this.contentWidth + this.xLocation;
                if (getWidth() - repeatXLocation >= this.textDistancePx) {
                    canvas.drawText(this.text, repeatXLocation + this.textDistancePx,
                            (float) (this.getMeasuredHeight() / 2) + this.textHeight / 2.0F,
                            this.paint);
                }
            }
        } else {
            stopRoll();
        }
    }

    public void setRepeatType(int repeatType) {
        this.repeatType = repeatType;
        this.resetInit = true;
        this.setText(this.text);
    }

    private Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            if (isRoll) {
                invalidate();
            }
        }
    };

    private void sendMarqueeMessage() {
        Choreographer.getInstance().removeFrameCallback(frameCallback);
        Choreographer.getInstance().postFrameCallback(frameCallback);
    }

    public void continueRoll() {
        if (!this.isRoll) {
            this.isRoll = true;
            sendMarqueeMessage();
        }
    }

    public void stopRoll() {
        this.isRoll = false;
    }

    private void setClickStop(boolean isClickStop) {
        this.isClickStop = isClickStop;
    }

    private void setContinueble(int isContinuable) {
        this.repeatType = isContinuable;
    }

    public void setTextDistance(int textDistance) {
        this.textDistance = textDistance;
        this.setText(this.text);
    }

    private float getTextWidth(String black) {
        if (black != null && black.length() > 0) {
            if (this.rect == null) {
                this.rect = new Rect();
            }

            this.paint.getTextBounds(black, 0, black.length(), this.rect);
            this.textHeight = this.getTextHeight();
            return (float) this.rect.width();
        } else {
            return 0.0F;
        }
    }

    private float getTextHeight() {
        Paint.FontMetrics fontMetrics = this.paint.getFontMetrics();
        return Math.abs(fontMetrics.bottom - fontMetrics.top) / 2.0F;
    }

    public void setTextColor(int textColor) {
        if (textColor != 0) {
            this.textColor = textColor;
            this.paint.setColor(textColor);
        }
    }

    public void setTextSize(float textSize) {
        if (textSize > 0.0F) {
            this.textSize = textSize;
            this.paint.setTextSize((float) this.dp2px(textSize));
            this.contentWidth = (int) this.getTextWidth(this.text);
        }
    }

    public void setTextSpeed(float speed) {
        this.speed = speed;
    }

    public void setText(String text) {
        if (!TextUtils.equals(this.text, text)) {
            if (this.isResetLocation) {
                this.xLocation = (float) this.getWidth() * this.startLocation;
            }
            this.text = text;
            this.contentWidth = (int) this.getTextWidth(this.text);
            if (!this.isRoll) {
                this.continueRoll();
            }
        }
    }
}

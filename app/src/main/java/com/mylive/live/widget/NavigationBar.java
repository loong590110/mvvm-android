package com.mylive.live.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mylive.live.R;

/**
 * Create by zailongshi on 2019/7/7
 */
public class NavigationBar extends FrameLayout {

    private View titleView, backButton, rightButton;

    public NavigationBar(Context context) {
        this(context, null);
    }

    public NavigationBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //obtain attrs
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.NavigationBar);
        int layoutResId = typedArray.getResourceId(R.styleable.NavigationBar_layout,
                R.layout.navigation_bar_default);
        String title = typedArray.getString(R.styleable.NavigationBar_title);
        Drawable backButtonIcon = typedArray.getDrawable(R.styleable.NavigationBar_backButtonIcon);
        String rightButtonText = typedArray.getString(R.styleable.NavigationBar_rightButtonText);
        typedArray.recycle();
        //initialize view
        LayoutInflater.from(context).inflate(layoutResId, this, true);
        titleView = findViewById(R.id.navigation_bar_title);
        backButton = findViewById(R.id.navigation_bar_back_button);
        rightButton = findViewById(R.id.navigation_bar_right_button);
        if (context instanceof OnBackButtonClickListener
                && backButton != null) {
            backButton.setOnClickListener(
                    ((OnBackButtonClickListener)
                            context)::onBackButtonClick);
        }
        setTitle(title);
        setRightButtonText(rightButtonText);
        if (backButton instanceof ImageView && null != backButtonIcon) {
            ((ImageView) backButton).setImageDrawable(backButtonIcon);
        }
    }

    public void setTitle(String title) {
        if (titleView instanceof TextView) {
            ((TextView) titleView).setText(title);
        }
    }

    public void setRightButtonText(String rightButtonText) {
        if (rightButton instanceof TextView) {
            ((TextView) rightButton).setText(rightButtonText);
        }
    }

    public void setOnRightButtonClickListener(OnClickListener l) {
        if (rightButton != null) {
            rightButton.setOnClickListener(l);
        }
    }

    public View getTitleView() {
        return titleView;
    }

    public View getBackButton() {
        return backButton;
    }

    public View getRightButton() {
        return rightButton;
    }

    public interface OnBackButtonClickListener {
        void onBackButtonClick(View v);
    }
}

package com.mylive.live.widget;

import android.view.View;

/**
 * Create by zailongshi on 2019/7/15
 */
public final class TabHost {

    private View[] tabs;
    private View selectedTab;
    private OnTabSelectedListener onTabSelectedListener;

    public static TabHost create() {
        return new TabHost();
    }

    private TabHost() {
    }

    public TabHost setTabs(View... tabs) {
        this.tabs = tabs;
        for (View tab : tabs) {
            tab.setOnClickListener(this::select);
        }
        return this;
    }

    public TabHost setOnTabSelectedListener(OnTabSelectedListener l) {
        this.onTabSelectedListener = l;
        return this;
    }

    public void select(View tab) {
        if (selectedTab == tab) {
            return;
        }
        if (selectedTab != null) {
            if (onTabSelectedListener != null) {
                onTabSelectedListener.onTabUnselected(selectedTab);
            }
        }
        if (onTabSelectedListener != null) {
            onTabSelectedListener.onTabSelected(tab);
        }
        selectedTab = tab;
    }

    public void select(int index) {
        select(tabs[index]);
    }

    public interface OnTabSelectedListener {
        void onTabSelected(View tab);

        void onTabUnselected(View tab);
    }
}

package com.mylive.live.widget;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

/**
 * Create by zailongshi on 2019/7/15
 */
public final class TabHost {

    private View[] tabs;
    private View selectedTab;
    private FragmentAdapter adapter;
    private OnTabSelectedListener onTabSelectedListener;

    public static TabHost create() {
        return new TabHost();
    }

    private TabHost() {
    }

    public TabHost setAdapter(FragmentAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public TabHost setTabs(View... tabs) {
        Objects.requireNonNull(tabs);
        this.tabs = tabs;
        for (View tab : tabs) {
            tab.setSelected(false);
            tab.setOnClickListener(this::select);
        }
        return this;
    }

    public TabHost setOnTabSelectedListener(OnTabSelectedListener l) {
        this.onTabSelectedListener = l;
        return this;
    }

    private void select(View tab) {
        Objects.requireNonNull(tab);
        if (selectedTab == tab) {
            return;
        }
        if (selectedTab != null) {
            selectedTab.setSelected(false);
            if (onTabSelectedListener != null) {
                onTabSelectedListener.onTabUnselected(selectedTab);
            }
        }
        if (onTabSelectedListener != null) {
            onTabSelectedListener.onTabSelected(tab);
        }
        if (adapter != null) {
            adapter.onCreateFragment(tab);
        }
        selectedTab = tab;
        selectedTab.setSelected(true);
    }

    public void select(int index) {
        select(tabs[index]);
    }

    public interface OnTabSelectedListener {
        void onTabSelected(View tab);

        void onTabUnselected(View tab);
    }

    public static abstract class FragmentAdapter {

        private FragmentHost host;

        public FragmentAdapter(FragmentManager fragmentManager,
                               @IdRes int fragmentContainerId) {
            host = FragmentHost.create(fragmentManager, fragmentContainerId);
        }

        private void onCreateFragment(View tab) {
            String tag = String.valueOf(System.identityHashCode(tab));
            Fragment fragment = host.get(tag);
            if (fragment == null) {
                fragment = getFragment(tab);
            }
            host.show(tag, fragment);
        }

        public abstract Fragment getFragment(View tab);
    }

    private static final class FragmentHost {

        private FragmentManager fragmentManager;
        private Fragment selectedFragment;
        @IdRes
        private int containerViewId;

        private static FragmentHost create(FragmentManager fragmentManager,
                                           @IdRes int containerViewId) {
            return new FragmentHost(fragmentManager, containerViewId);
        }

        private FragmentHost(FragmentManager fragmentManager,
                             @IdRes int containerViewId) {
            this.fragmentManager = fragmentManager;
            this.containerViewId = containerViewId;
        }

        private Fragment get(String tag) {
            return fragmentManager.findFragmentByTag(tag);
        }

        private void show(String tag, Fragment fragment) {
            Objects.requireNonNull(fragment);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (get(tag) == null) {
                transaction.add(containerViewId, fragment, tag);
            }
            if (selectedFragment != null) {
                transaction.hide(selectedFragment);
            }
            transaction.show(fragment).commitAllowingStateLoss();
            selectedFragment = fragment;
        }
    }
}

package com.mylive.live.widget;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 若要支持fitsSystemWindows属性，
 * 需用{@link FitsSystemWindowsFrameLayout}
 * 控件作为Fragment的容器
 * Create by zailongshi on 2019/7/15
 */
public final class TabHost {

    private View[] tabs;
    private View selectedTab;
    private int selectedIndex;
    private FragmentAdapter adapter;
    private OnTabSelectedListener onTabSelectedListener;

    public static TabHost create() {
        return new TabHost();
    }

    private TabHost() {
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public TabHost setAdapter(FragmentAdapter adapter) {
        if (this.adapter != null) {
            throw new IllegalStateException("TabHost的Adapter只能设置一次");
        }
        this.adapter = adapter;
        return this;
    }

    public TabHost setTabs(View... tabs) {
        Objects.requireNonNull(tabs);
        diffAndDispose(this.tabs, tabs);
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

    private void diffAndDispose(View[] tabsOld, View[] tabsNew) {
        if (adapter == null || tabsOld == null) {
            return;
        }
        List<View> tabs = new ArrayList<>();
        for (View tab : tabsOld) {
            int index = indexOf(tab, tabsNew);
            if (index == -1) {
                tabs.add(tab);
            }
        }
        adapter.remove(tabs.toArray(new View[]{}));
    }

    private int indexOf(View tab, View[] tabs) {
        for (int i = 0; i < tabs.length; i++) {
            if (tab == tabs[i]) {
                return i;
            }
        }
        return -1;
    }

    public void select(View tab) {
        Objects.requireNonNull(tab);
        if (selectedTab == tab) {
            return;
        }
        int index = indexOf(tab, tabs);
        if (index == -1) {
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
        selectedIndex = index;
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
                               @IdRes int containerViewId) {
            host = FragmentHost.create(fragmentManager, containerViewId);
        }

        private void onCreateFragment(View tab) {
            String tag = String.valueOf(System.identityHashCode(tab));
            Fragment fragment = host.get(tag);
            if (fragment == null) {
                fragment = getFragment(tab);
            }
            host.show(tag, fragment);
        }

        private void remove(View... tabs) {
            if (tabs == null || tabs.length == 0) {
                return;
            }
            List<Fragment> fragments = new ArrayList<>();
            for (View tab : tabs) {
                String tag = String.valueOf(System.identityHashCode(tab));
                fragments.add(host.get(tag));
            }
            host.remove(fragments.toArray(new Fragment[]{}));
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
                if (selectedFragment == null) {
                    transaction.replace(containerViewId, fragment, tag);
                } else {
                    transaction.add(containerViewId, fragment, tag);
                }
            }
            if (selectedFragment != null) {
                transaction.hide(selectedFragment);
            }
            transaction.show(fragment).commitAllowingStateLoss();
            selectedFragment = fragment;
        }

        private void remove(Fragment... fragments) {
            if (fragments == null || fragments.length == 0) {
                return;
            }
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            for (Fragment fragment : fragments) {
                if (fragment != null) {
                    transaction.remove(fragment);
                }
            }
            transaction.commitAllowingStateLoss();
        }
    }
}

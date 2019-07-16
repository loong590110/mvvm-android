package com.mylive.live.widget;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by zailongshi on 2019/7/15
 */
public final class FragmentHost {

    private FragmentManager fragmentManager;
    private Fragment selectedFragment;
    @IdRes
    private int fragmentContainerId;
    private boolean keep, remove;
    private List<Class<? extends Fragment>> willBeRemovedFragments;

    public static FragmentHost create(FragmentManager fragmentManager,
                                      @IdRes int fragmentContainerId) {
        return new FragmentHost(fragmentManager, fragmentContainerId);
    }

    private FragmentHost(FragmentManager fragmentManager,
                         @IdRes int fragmentContainerId) {
        this.fragmentManager = fragmentManager;
        this.fragmentContainerId = fragmentContainerId;
    }

    public FragmentHost keep() {
        keep = true;
        return this;
    }

    public FragmentHost remove() {
        remove = true;
        return this;
    }

    public FragmentHost remove(Class<? extends Fragment> fragment) {
        if (willBeRemovedFragments == null) {
            willBeRemovedFragments = new ArrayList<>();
        }
        willBeRemovedFragments.add(fragment);
        return this;
    }

    public void show(Context context, Class<? extends Fragment> fragment, Bundle args) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        String fragmentName = fragment.getName();
        Fragment fragment1 = fragmentManager.findFragmentByTag(fragmentName);
        if (fragment1 == null) {
            fragment1 = Fragment.instantiate(context, fragmentName, args);
            transaction.add(fragmentContainerId, fragment1, fragmentName);
        }
        for (Class<? extends Fragment> willBeRemovedFragment : willBeRemovedFragments) {
            String fragmentName2 = willBeRemovedFragment.getName();
            Fragment fragment2 = fragmentManager.findFragmentByTag(fragmentName2);
            if (fragment2 != null) {
                transaction.remove(fragment2);
            }
        }
        boolean selectedFragmentRemoved = willBeRemovedFragments != null
                && willBeRemovedFragments.contains(selectedFragment.getClass());
        if (remove && selectedFragment != null
                && !selectedFragmentRemoved) {
            transaction.remove(selectedFragment);
        } else if (!keep && selectedFragment != null
                && !selectedFragmentRemoved) {
            transaction.hide(selectedFragment);
        }
        transaction.show(fragment1).commitAllowingStateLoss();
        selectedFragment = fragment1;
        remove = false;
        keep = false;
    }

    public void show(Context context, Class<? extends Fragment> fragment) {
        show(context, fragment, null);
    }
}

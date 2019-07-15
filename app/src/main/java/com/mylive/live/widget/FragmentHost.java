package com.mylive.live.widget;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Create by zailongshi on 2019/7/15
 */
public final class FragmentHost {

    private FragmentManager fragmentManager;
    private Fragment selectedFragment;
    @IdRes
    private int fragmentContainerId;

    public static FragmentHost create(FragmentManager fragmentManager,
                                      @IdRes int fragmentContainerId) {
        return new FragmentHost(fragmentManager, fragmentContainerId);
    }

    private FragmentHost(FragmentManager fragmentManager,
                         @IdRes int fragmentContainerId) {
        this.fragmentManager = fragmentManager;
        this.fragmentContainerId = fragmentContainerId;
    }

    public void show(Context context, Class<? extends Fragment> fragment, Bundle args) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        String fragmentName = fragment.getName();
        Fragment fragment1 = fragmentManager.findFragmentByTag(fragmentName);
        if (fragment1 == null) {
            fragment1 = Fragment.instantiate(context, fragmentName, args);
            transaction.add(fragmentContainerId, fragment1, fragmentName);
        }
        if (selectedFragment != null) {
            transaction.hide(selectedFragment);
        }
        transaction.show(fragment1).commitAllowingStateLoss();
        selectedFragment = fragment1;
    }

    public void show(Context context, Class<? extends Fragment> fragment) {
        show(context, fragment, null);
    }
}

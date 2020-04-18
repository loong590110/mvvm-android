package com.mylive.live.view.room;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mylive.live.R;
import com.mylive.live.databinding.DialogGiftsBinding;

/**
 * Created by Developer Zailong Shi on 2020/4/18.
 */
public class GiftsDialogFragment extends BottomSheetDialogFragment {

    public static class Builder {
        private Bundle arguments = new Bundle();

        public Builder setTitle(String title) {
            arguments.putString("title", title);
            return this;
        }

        public GiftsDialogFragment build() {
            GiftsDialogFragment fragment = new GiftsDialogFragment();
            fragment.setArguments(arguments);
            return fragment;
        }
    }

    private DialogGiftsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return (binding = DataBindingUtil.inflate(
                inflater, R.layout.dialog_gifts, container, false
        )).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.viewPager.setAdapter(new FragmentPagerAdapter(
                getChildFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return GiftsFragment.newInstance();
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return "Tab " + (position + 1);
            }
        });
    }
}

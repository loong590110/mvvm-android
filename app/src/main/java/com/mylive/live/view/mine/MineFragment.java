package com.mylive.live.view.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.mylive.live.R;
import com.mylive.live.arch.annotation.FieldMap;
import com.mylive.live.arch.subscriber.Scheduler;
import com.mylive.live.base.BaseFragment;
import com.mylive.live.databinding.FragmentMineBinding;
import com.mylive.live.model.beans.HttpResp;

/**
 * Created by Developer Zailong Shi on 2019-06-28.
 */
public class MineFragment extends BaseFragment {

    @FieldMap("binding")
    FragmentMineBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return (binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_mine, container, false)
        ).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    protected void onSubscribe(Scheduler scheduler) {
        super.onSubscribe(scheduler);
        scheduler.subscribe(HttpResp.class, httpResp -> {

        });
    }
}

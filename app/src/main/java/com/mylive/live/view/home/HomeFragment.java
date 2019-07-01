package com.mylive.live.view.home;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mylive.live.R;
import com.mylive.live.arch.annotation.FieldMap;
import com.mylive.live.arch.subscriber.Scheduler;
import com.mylive.live.base.BaseFragment;
import com.mylive.live.databinding.FragmentHomeBinding;
import com.mylive.live.model.HttpResponse;

/**
 * Created by Developer Zailong Shi on 2019-06-28.
 */
public class HomeFragment extends BaseFragment {

    @FieldMap("binding")
    FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return (binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_home, container, false)
        ).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.txtHome.setText("home fragment + 2");
        getFeaturesManager().put(LiveListFeature.class);
    }

    @Override
    protected void onSubscribe(Scheduler scheduler) {
        super.onSubscribe(scheduler);
        scheduler.subscribe(HttpResponse.class, httpResponse -> {
            binding.txtHome.setText(httpResponse.toString());
        });
    }
}

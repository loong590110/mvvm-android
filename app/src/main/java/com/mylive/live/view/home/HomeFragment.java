package com.mylive.live.view.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mylive.live.R;
import com.mylive.live.arch.annotation.Features;
import com.mylive.live.arch.annotation.FieldMap;
import com.mylive.live.arch.subscriber.Scheduler;
import com.mylive.live.base.BaseFragment;
import com.mylive.live.databinding.FragmentHomeBinding;
import com.mylive.live.model.HttpResp;

/**
 * Created by Developer Zailong Shi on 2019-06-28.
 */
@Features({
        LiveListFeature.class
})
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
        int bottomBarHeight = getResources().getDimensionPixelSize(R.dimen.tab_bar_main_height);
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (recyclerView.getChildCount() > 0) {
                        boolean canScroll = recyclerView.canScrollVertically(1)
                                || recyclerView.canScrollVertically(-1);
                        View lastItem = recyclerView.getChildAt(
                                recyclerView.getChildCount() - 1);
                        int height = recyclerView.getHeight();
                        int bottom = lastItem.getBottom();
                        if (!canScroll && height - bottom < bottomBarHeight) {
                            HomeScrollEvent.getInstance().toggle();
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                HomeScrollEvent.getInstance().onScrolled(dy);
            }
        });
    }

    @Override
    protected void onSubscribe(Scheduler scheduler) {
        super.onSubscribe(scheduler);
        scheduler.subscribe(HttpResp.class, httpResp -> {

        });
    }
}

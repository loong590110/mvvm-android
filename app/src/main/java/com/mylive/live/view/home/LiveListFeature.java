package com.mylive.live.view.home;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.mylive.live.R;
import com.mylive.live.arch.annotation.FieldMap;
import com.mylive.live.arch.feature.FeaturesFragment;
import com.mylive.live.base.BaseFeature;
import com.mylive.live.base.BaseViewHolder;
import com.mylive.live.databinding.FragmentHomeBinding;
import com.mylive.live.databinding.ItemLiveListBinding;

import java.util.Random;

/**
 * Created by Developer Zailong Shi on 2019-07-01.
 */
public class LiveListFeature extends BaseFeature {

    @FieldMap("binding")
    FragmentHomeBinding binding;

    public LiveListFeature(FeaturesFragment fragment) {
        super(fragment);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onViewCreated() {
        binding.navigationBar.setTitle("hello, live list feature.");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        binding.recyclerView.setLayoutManager(gridLayoutManager);
        binding.recyclerView.setAdapter(new LiveListAdapter());
    }

    private class LiveListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

        @NonNull
        @Override
        public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ItemViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder viewHolder, int position) {
            if (viewHolder instanceof ItemViewHolder) {
                ((ItemViewHolder) viewHolder).onBindViewHolder(position);
            }
        }

        @Override
        public int getItemCount() {
            return 10000;
        }
    }

    private class ItemViewHolder extends BaseViewHolder {

        private ItemLiveListBinding binding;

        private ItemViewHolder(@NonNull ViewGroup parent) {
            super(parent, R.layout.item_live_list);
            binding = DataBindingUtil.bind(itemView);
        }

        private void onBindViewHolder(int position) {
            binding.txtContent.setText("hello, item " + position);
            binding.txtContent.setBackgroundColor(0xff000000 |
                    ((int) (0xffffff * new Random().nextFloat())));
        }
    }
}

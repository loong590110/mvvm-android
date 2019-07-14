package com.mylive.live.view.home;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ViewGroup;

import com.mylive.live.R;
import com.mylive.live.arch.annotation.FieldMap;
import com.mylive.live.arch.annotation.ViewModel;
import com.mylive.live.arch.feature.FeaturesFragment;
import com.mylive.live.base.BaseFeature;
import com.mylive.live.base.BaseViewHolder;
import com.mylive.live.databinding.FragmentHomeBinding;
import com.mylive.live.databinding.ItemLiveListBinding;
import com.mylive.live.model.Config;
import com.mylive.live.model.LiveList;
import com.mylive.live.router.WebActivityStarter;
import com.mylive.live.viewmodel.LiveListViewModel;

import java.util.Random;

/**
 * Created by Developer Zailong Shi on 2019-07-01.
 */
public class LiveListFeature extends BaseFeature {

    @FieldMap("binding")
    FragmentHomeBinding binding;
    @ViewModel
    LiveListViewModel liveListViewModel;
    private LiveListAdapter liveListAdapter;
    private int itemHeight;

    public LiveListFeature(FeaturesFragment fragment) {
        super(fragment);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onViewCreated() {
        binding.navigationBar.setTitle("live list feature.");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        binding.recyclerView.setLayoutManager(gridLayoutManager);
        binding.recyclerView.setAdapter(liveListAdapter = new LiveListAdapter());
        liveListViewModel.getLiveList(false, 2000)
                .observe(getLifecycleOwner(), liveList -> {
                    itemHeight = binding.recyclerView.getHeight() / 4 + 10;
                    liveListAdapter.setLiveList(liveList);
                });
    }

    private class LiveListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

        private LiveList liveList;

        @NonNull
        @Override
        public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ItemViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder viewHolder, int position) {
            if (viewHolder instanceof ItemViewHolder) {
                ((ItemViewHolder) viewHolder).onBindViewHolder(position,
                        liveList.list.get(position));
            }
        }

        @Override
        public int getItemCount() {
            if (liveList == null || liveList.list == null)
                return 0;
            if (liveList.list.size() > 12) {
                //return 12;//测试ScrollEvent
            }
            return liveList.list.size();
        }

        public void setLiveList(LiveList liveList) {
            this.liveList = liveList;
            notifyDataSetChanged();
        }
    }

    private class ItemViewHolder extends BaseViewHolder {

        private ItemLiveListBinding binding;

        private ItemViewHolder(@NonNull ViewGroup parent) {
            super(parent, R.layout.item_live_list);
            binding = DataBindingUtil.bind(itemView);
        }

        private void onBindViewHolder(int position, LiveList.LiveListItem item) {
            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            params.height = itemHeight;
            itemView.setLayoutParams(params);
            binding.txtContent.setText(item.desc);
            binding.txtContent.setBackgroundColor(0xff000000 |
                    ((int) (0xffffff * new Random().nextFloat())));
            binding.getRoot().setOnClickListener(v -> {
                WebActivityStarter.create(Config.instance().homePage)
                        .start(LiveListFeature.this);
            });
        }
    }
}

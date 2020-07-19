package com.mylive.live.view.news;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mylive.live.R;
import com.mylive.live.arch.annotation.FieldMap;
import com.mylive.live.arch.annotation.ViewModel;
import com.mylive.live.arch.subscriber.Scheduler;
import com.mylive.live.base.BaseFragment;
import com.mylive.live.base.BaseViewHolder;
import com.mylive.live.databinding.FragmentNewsBinding;
import com.mylive.live.databinding.ItemLiveListBinding;
import com.mylive.live.imageloader.ImageLoader;
import com.mylive.live.model.beans.HttpResp;
import com.mylive.live.model.beans.LiveList;
import com.mylive.live.router.LiveRoomActivityStarter;
import com.mylive.live.utils.DensityUtils;
import com.mylive.live.utils.LoadMoreHelper;
import com.mylive.live.utils.ToastUtils;
import com.mylive.live.viewmodel.LiveListViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kotlin.Unit;

/**
 * Created by Developer Zailong Shi on 2019-06-28.
 */
public class NewsFragment extends BaseFragment {

    @FieldMap("binding")
    FragmentNewsBinding binding;
    @ViewModel
    private LiveListViewModel liveListViewModel;
    private LoadMoreHelper loadMoreHelper;
    private LiveListAdapter adapter;
    private int pageIndex = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return (binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_news, container, false)
        ).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recyclerView.setBackgroundResource(R.color.backgroundBorderColor);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter = new LiveListAdapter());
        binding.recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            final int space = DensityUtils.dp2px(getContext(), 10);

            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View child,
                                       @NonNull RecyclerView parent,
                                       @NonNull RecyclerView.State state) {
                final int position = parent.getChildLayoutPosition(child);
                outRect.set(0, position > 0 ? space : 0, 0, 0);
            }
        });
        adapter.setOnItemClickListener((position, item) -> {
            LiveRoomActivityStarter.create().start(this);
        });
        loadMoreHelper = LoadMoreHelper.create(binding.recyclerView, true);
        loadMoreHelper.setOnLoadMoreListener(() -> loadData(false));
        loadData(true);
    }

    @Override
    protected void onSubscribe(Scheduler scheduler) {
        super.onSubscribe(scheduler);
        scheduler.subscribe(HttpResp.class, httpResp -> {

        });
    }

    private void loadData(boolean refresh) {
        if (!refresh) {
            pageIndex += 1;
        } else {
            pageIndex = 1;
        }
        liveListViewModel.getLiveList(pageIndex, 20)
                .observe(this, liveList -> {
                    loadMoreHelper.setLoading(false);
                    if (liveList == null) {
                        ToastUtils.showShortToast(getContext(), R.string.network_exception);
                        return Unit.INSTANCE;
                    }
                    adapter.setData(liveList.list, !refresh);
                    return Unit.INSTANCE;
                });
    }

    private class LiveListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

        private List<LiveList.LiveListItem> data;
        private OnItemClickListener onItemClickListener;

        @NonNull
        @Override
        public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ItemViewHolder(parent).setOnItemClickListener(
                    onItemClickListener
            );
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder viewHolder, int position) {
            if (viewHolder instanceof ItemViewHolder) {
                ((ItemViewHolder) viewHolder).onBindViewHolder(position, data.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        public void setData(List<LiveList.LiveListItem> data, boolean append) {
            if (this.data == null) {
                this.data = new ArrayList<>();
            }
            if (!append) {
                this.data.clear();
            }
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }
    }

    private class ItemViewHolder extends BaseViewHolder {

        private ItemLiveListBinding binding;
        private OnItemClickListener onItemClickListener;

        private ItemViewHolder(@NonNull ViewGroup parent) {
            super(parent, R.layout.item_live_list);
            binding = DataBindingUtil.bind(itemView);
        }

        private void onBindViewHolder(int position, LiveList.LiveListItem item) {
            if (item.dimenRatio < 1f) {
                item.dimenRatio = new Random().nextFloat() + 1f;
            }
            ConstraintLayout.LayoutParams params =
                    (ConstraintLayout.LayoutParams) binding.imgCover.getLayoutParams();
            params.dimensionRatio = String.valueOf(item.dimenRatio);
            binding.imgCover.setLayoutParams(params);
            ImageLoader.getInstance().display(binding.imgCover, item.cover);
            binding.txtContent.setText(item.desc);
            binding.getRoot().setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, item);
                }
            });
            binding.getRoot().setBackgroundResource(R.color.backgroundColor);
            final int space = DensityUtils.dp2px(getContext(), 10);
            binding.getRoot().setPadding(space, space, space, space);
        }

        public ItemViewHolder setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
            return this;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, LiveList.LiveListItem item);
    }
}

package com.mylive.live.view.home;

import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mylive.live.R;
import com.mylive.live.arch.annotation.FieldMap;
import com.mylive.live.arch.annotation.ViewModel;
import com.mylive.live.arch.feature.FeaturesManagerOwner;
import com.mylive.live.arch.thread.ThreadsScheduler;
import com.mylive.live.base.BaseFeature;
import com.mylive.live.base.BaseViewHolder;
import com.mylive.live.databinding.FragmentHomeTabBinding;
import com.mylive.live.databinding.ItemBannerBinding;
import com.mylive.live.databinding.ItemLiveListBinding;
import com.mylive.live.model.Config;
import com.mylive.live.model.LiveList;
import com.mylive.live.router.WebActivityStarter;
import com.mylive.live.utils.LoadMoreHelper;
import com.mylive.live.utils.Timer;
import com.mylive.live.utils.ToastUtils;
import com.mylive.live.viewmodel.LiveListViewModel;
import com.mylive.live.widget.PagingScrollHelper;
import com.mylive.live.widget.CarouselViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Developer Zailong Shi on 2019-07-01.
 */
public class LiveListFeature extends BaseFeature {

    @FieldMap("type")
    private int type;
    @FieldMap("binding")
    private FragmentHomeTabBinding binding;
    @ViewModel
    private LiveListViewModel liveListViewModel;
    private LiveListAdapter liveListAdapter;
    private LoadMoreHelper loadMoreHelper;
    private int itemHeight;

    public LiveListFeature(FeaturesManagerOwner owner) {
        super(owner);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onViewCreated() {
        binding.refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        binding.refreshLayout.setOnRefreshListener(() -> loadData(true));
        binding.refreshLayout.setRefreshing(true);
        PagingScrollHelper pagingScrollHelper = new PagingScrollHelper(6);
        pagingScrollHelper.attachToRecyclerView(binding.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                getContext(), 3
        );
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int spanCount = gridLayoutManager.getSpanCount();
                if (position == 0) {
                    return spanCount;
                }
                return 1;
            }
        });
        binding.recyclerView.setLayoutManager(gridLayoutManager);
        binding.recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                       @NonNull RecyclerView parent,
                                       @NonNull RecyclerView.State state) {
                outRect.set(35, 35, 35, 35);
            }
        });
        binding.recyclerView.setAdapter(
                liveListAdapter = new LiveListAdapter().setOnItemClickListener(
                        (position, item) -> {
                            pagingScrollHelper.setCurrentPageIndex(
                                    position / pagingScrollHelper.getPageSize()
                            );
                        }
                )
        );
        binding.loadingView.setForegroundColorRes(R.color.colorPrimary);
        loadMoreHelper = LoadMoreHelper.create(binding.recyclerView, true);
        loadMoreHelper.setOnLoadMoreListener(() -> loadData(false));
        loadData(true);
    }

    private void loadData(boolean refresh) {
        if (!refresh) {
            binding.loadingView.setLoading(true);
        }
        Timer timer = Timer.start();
        liveListViewModel.getLiveList(type, 60)
                .observe(getLifecycleOwner(), liveList -> {
                    ThreadsScheduler.runOnUiThread(() -> {
                        if (refresh) {
                            binding.refreshLayout.setRefreshing(false);
                        } else {
                            binding.loadingView.setLoading(false);
                            loadMoreHelper.setLoading(false);
                        }
                    }, 1500 - timer.end());
                    Timer.recycle(timer);
                    if (liveList == null) {
                        ToastUtils.showShortToast(getContext(), R.string.network_exception);
                        return;
                    }
                    itemHeight = binding.recyclerView.getHeight() / 4;
                    liveListAdapter.setData(liveList.list, !refresh);
                });
    }

    private class LiveListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

        private final int VIEW_TYPE_NORMAL = 0, VIEW_TYPE_BANNER = 1;
        private List<LiveList.LiveListItem> data;
        private OnItemClickListener onItemClickListener;

        @NonNull
        @Override
        public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_BANNER) {
                return new BannerViewHolder(parent);
            }
            return new ItemViewHolder(parent).setOnItemClickListener(onItemClickListener);
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder viewHolder, int position) {
            if (viewHolder instanceof ItemViewHolder) {
                ((ItemViewHolder) viewHolder).onBindViewHolder(position, data.get(position - VIEW_TYPE_BANNER));
            } else if (viewHolder instanceof BannerViewHolder) {
                ((BannerViewHolder) viewHolder).onBindViewHolder(position);
            }
        }

        @Override
        public int getItemCount() {
            if (data == null) {
                return VIEW_TYPE_BANNER;
            }
            if (data.size() > 12) {
                //return 12;//测试ScrollEvent
            }
            return data.size() + VIEW_TYPE_BANNER;
        }

        @Override
        public int getItemViewType(int position) {
            if (position < VIEW_TYPE_BANNER) {
                return VIEW_TYPE_BANNER;
            }
            return VIEW_TYPE_NORMAL;
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

        public LiveListAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
            return this;
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
//            ViewGroup.LayoutParams params = itemView.getLayoutParams();
//            params.height = itemHeight;
//            itemView.setLayoutParams(params);
            binding.txtContent.setText(String.format(Locale.getDefault(),
                    "%s-%d", item.desc, position));
            binding.getRoot().setOnClickListener(v -> {
                if (position < 3) {
                    WebActivityStarter.create(Config.instance().homePage)
                            .start(LiveListFeature.this);
                }
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, item);
                }
            });
            int color = (int) (new Random().nextFloat() * 0xffffff) + 0xff000000;
            binding.getRoot().setBackgroundColor(color);
        }

        public ItemViewHolder setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
            return this;
        }
    }

    private class BannerViewHolder extends BaseViewHolder {

        private ItemBannerBinding binding;

        private BannerViewHolder(@NonNull ViewGroup parent) {
            super(parent, R.layout.item_banner);
            binding = DataBindingUtil.bind(itemView);
        }

        private void onBindViewHolder(int position) {
            List<String> banners = new ArrayList<>();
            CarouselViewPager.Adapter adapter;
            binding.viewPager.setAdapter(adapter = new CarouselViewPager.Adapter<CarouselViewPager.ViewHolder>() {
                @Override
                public int getCount() {
                    return banners.size();
                }

                @Override
                protected CarouselViewPager.ViewHolder onCreateViewHolder(Context context) {
                    TextView view = new TextView(context);
                    view.setGravity(Gravity.CENTER);
                    return new CarouselViewPager.ViewHolder(view) {
                    };
                }

                @Override
                protected void onBindViewHolder(CarouselViewPager.ViewHolder holder, int position) {
                    ((TextView) holder.itemView).setText(banners.get(position));
                    holder.itemView.setBackgroundColor((int) (0xff000000 + new Random().nextFloat() * 0xffffff));
                }
            });
            for (int i = 0; i < 10; i++) {
                banners.add("ad " + (1 + i));
            }
            adapter.notifyDataSetChanged();
            binding.viewPager.setInterval(3000).setAnimationDuration(500).play();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, LiveList.LiveListItem item);
    }
}

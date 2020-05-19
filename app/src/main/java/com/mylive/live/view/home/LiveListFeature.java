package com.mylive.live.view.home;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.mylive.live.R;
import com.mylive.live.arch.annotation.FieldMap;
import com.mylive.live.arch.annotation.ViewModel;
import com.mylive.live.arch.feature.FeaturesManagerOwner;
import com.mylive.live.arch.thread.ThreadsScheduler;
import com.mylive.live.base.BaseFeature;
import com.mylive.live.base.BaseViewHolder;
import com.mylive.live.databinding.FragmentHomeTabBinding;
import com.mylive.live.databinding.ItemAvatarBinding;
import com.mylive.live.databinding.ItemBannerBinding;
import com.mylive.live.databinding.ItemLiveListBinding;
import com.mylive.live.imageloader.ImageLoader;
import com.mylive.live.model.LiveList;
import com.mylive.live.router.LiveRoomActivityStarter;
import com.mylive.live.utils.DensityUtils;
import com.mylive.live.utils.LoadMoreHelper;
import com.mylive.live.utils.Timer;
import com.mylive.live.utils.ToastUtils;
import com.mylive.live.viewmodel.LiveListViewModel;
import com.mylive.live.widget.CarouselViewPager;
import com.mylive.live.widget.SpinGallery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
    private int pageIndex = 1;

    public LiveListFeature(FeaturesManagerOwner owner) {
        super(owner);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onViewCreated() {
        binding.refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        binding.refreshLayout.setOnRefreshListener(() -> loadData(true));
        binding.refreshLayout.setRefreshing(true);
//        PagingScrollHelper pagingScrollHelper = new PagingScrollHelper(6);
//        pagingScrollHelper.attachToRecyclerView(binding.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return gridLayoutManager.getSpanCount();
                }
                return 1;
            }
        });
        binding.recyclerView.setLayoutManager(gridLayoutManager);
        binding.recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View child,
                                       @NonNull RecyclerView parent,
                                       @NonNull RecyclerView.State state) {
                final GridLayoutManager.SpanSizeLookup lookup = gridLayoutManager.getSpanSizeLookup();
                final int spanCount = gridLayoutManager.getSpanCount();
                final int space = DensityUtils.dp2px(getContext(), 10);
                final int position = parent.getChildLayoutPosition(child);
                final int groupIndex = lookup.getSpanGroupIndex(position, spanCount);
                final int spanIndex = lookup.getSpanIndex(position, spanCount);
                final int spanSize = lookup.getSpanSize(position);
                final int top = groupIndex == 0 ? space : 0;
                final int bottom = space;
                final int left = position == 0 ? 0 : spanIndex == 0 ? space : space / 2;
                final int right = position == 0 ? 0 : spanSize == spanCount ? space : spanIndex == 0 ? space / 2 : space;
                outRect.set(left, top, right, bottom);
            }
        });
        binding.recyclerView.setAdapter(
                liveListAdapter = new LiveListAdapter().setOnItemClickListener(
                        (position, item) -> {
//                            pagingScrollHelper.setCurrentPageIndex(
//                                    position / pagingScrollHelper.getPageSize()
//                            );
                            LiveRoomActivityStarter.create().start(this);
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
            pageIndex += 1;
        } else {
            pageIndex = 1;
        }
        Timer timer = Timer.start();
        liveListViewModel.getLiveList(pageIndex, 20)
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
            ImageLoader.getInstance().display(binding.imgCover, item.cover);
            binding.txtContent.setText(item.desc);
            binding.getRoot().setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, item);
                }
            });
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
            if (binding.viewPager.getAdapter() == null) {
                binding.viewPager.setAdapter(new CarouselViewPager.Adapter<CarouselViewPager.ViewHolder>() {
                    String[] banners = {
                            "https://i0.hdslb.com/bfs/archive/d60ae7764ef61d862843d5d0fd0094778d2e9937.jpg@480w_300h.webp",
                            "https://i0.hdslb.com/bfs/archive/18c9a8bb1e2c5bf27a467f716bef60ba4e21f4e3.png@480w_300h.webp",
//                            "https://i0.hdslb.com/bfs/archive/9a4892084bbc77f141f50139dc05651f020cbae8.png@480w_300h.webp",
//                            "https://i0.hdslb.com/bfs/archive/c5c918d385205afd646bcc2ca8e978765d34991f.png@480w_300h.webp",
//                            "https://i0.hdslb.com/bfs/archive/c5c918d385205afd646bcc2ca8e978765d34991f.png@480w_300h.webp",
//                            "https://i0.hdslb.com/bfs/archive/c5c918d385205afd646bcc2ca8e978765d34991f.png@480w_300h.webp",
//                            "https://i0.hdslb.com/bfs/archive/c5c918d385205afd646bcc2ca8e978765d34991f.png@480w_300h.webp",
//                            "https://i0.hdslb.com/bfs/archive/c5c918d385205afd646bcc2ca8e978765d34991f.png@480w_300h.webp",
//                            "https://i0.hdslb.com/bfs/archive/c5c918d385205afd646bcc2ca8e978765d34991f.png@480w_300h.webp",
//                            "https://i0.hdslb.com/bfs/archive/c5c918d385205afd646bcc2ca8e978765d34991f.png@480w_300h.webp",
//                            "https://i0.hdslb.com/bfs/archive/c5c918d385205afd646bcc2ca8e978765d34991f.png@480w_300h.webp",
//                            "https://i0.hdslb.com/bfs/archive/c5c918d385205afd646bcc2ca8e978765d34991f.png@480w_300h.webp",
//                            "https://i0.hdslb.com/bfs/archive/c5c918d385205afd646bcc2ca8e978765d34991f.png@480w_300h.webp",
//                            "https://i0.hdslb.com/bfs/archive/c5c918d385205afd646bcc2ca8e978765d34991f.png@480w_300h.webp"
                    };

                    @Override
                    public int getCount() {
                        return banners.length;
                    }

                    @Override
                    protected CarouselViewPager.ViewHolder onCreateViewHolder(Context context) {
                        SimpleDraweeView view = new SimpleDraweeView(context);
                        view.getHierarchy().setRoundingParams(RoundingParams.fromCornersRadius(
                                DensityUtils.dp2px(context, 10.f)
                        ));
                        return new CarouselViewPager.ViewHolder(view) {
                        };
                    }

                    @Override
                    protected void onBindViewHolder(CarouselViewPager.ViewHolder holder, int position) {
                        ImageLoader.getInstance().display(
                                (ImageView) holder.itemView, banners[position]
                        );
                    }
                });
                binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    private int state;

                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        final int itemCount = binding.marqueeViewPager.getAdapter().getItemCount();
                        if (itemCount == 2 && position == 0
                                && state != ViewPager.SCROLL_STATE_DRAGGING) {
                            binding.marqueeViewPager.forward();
                        } else {
                            binding.marqueeViewPager.setCurrentItem(position % itemCount);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        if (state == ViewPager.SCROLL_STATE_DRAGGING
                                || state == ViewPager.SCROLL_STATE_IDLE) {
                            this.state = state;
                        }
                    }
                });
                binding.viewPager.setInterval(3000).setAnimationDuration(500).play();
            }
            if (binding.marqueeViewPager.getAdapter() == null) {
                binding.marqueeViewPager.setAdapter(new SpinGallery.Adapter<AvatarViewHolder>() {
                    private String[] avatars = {
                            UriUtil.getUriForResourceId(R.drawable.ic_avatar1).toString(),
                            UriUtil.getUriForResourceId(R.drawable.ic_avatar2).toString(),
//                            UriUtil.getUriForResourceId(R.drawable.ic_avatar3).toString(),
//                            UriUtil.getUriForResourceId(R.drawable.ic_avatar7).toString()
                    };

                    @NotNull
                    @Override
                    public AvatarViewHolder onCreateViewHolder(@NotNull ViewGroup parent) {
                        return new AvatarViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.item_avatar, parent, false
                        ));
                    }

                    @Override
                    public void onBindViewHolder(@NotNull AvatarViewHolder holder, int position) {
                        ImageLoader.getInstance().display(holder.binding.imgAvatar, avatars[position]);
                    }

                    @Override
                    public int getItemCount() {
                        return avatars.length;
                    }
                });
            }
        }

        class AvatarViewHolder extends SpinGallery.ViewHolder {
            private ItemAvatarBinding binding;

            AvatarViewHolder(@NotNull View itemView) {
                super(itemView);
                binding = DataBindingUtil.bind(itemView);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, LiveList.LiveListItem item);
    }
}

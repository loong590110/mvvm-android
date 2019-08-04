package com.mylive.live.view.home;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import com.mylive.live.databinding.ItemLiveListBinding;
import com.mylive.live.model.Config;
import com.mylive.live.model.LiveList;
import com.mylive.live.router.WebActivityStarter;
import com.mylive.live.utils.DensityUtils;
import com.mylive.live.utils.LoadMoreHelper;
import com.mylive.live.utils.Timer;
import com.mylive.live.utils.ToastUtils;
import com.mylive.live.viewmodel.LiveListViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                getContext(), 3
        );
        binding.recyclerView.setLayoutManager(gridLayoutManager);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(
                getContext(), GridLayoutManager.VERTICAL) {
            private Paint paint;

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                setOrientation(GridLayoutManager.HORIZONTAL);
                super.onDraw(c, parent, state);
                setOrientation(GridLayoutManager.VERTICAL);
                super.onDraw(c, parent, state);
            }

            @Override
            public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
                super.onDrawOver(c, parent, state);
                if (paint == null) {
                    paint = new Paint();
                    paint.setStrokeWidth(DensityUtils.dp2px(getContext(), 1.5f));
                    paint.setColor(getContext().getResources().getColor(
                            R.color.colorAccent));
                }
                c.drawLine(0, 0, parent.getWidth(), 0, paint);
                c.drawLine(0, 0, 0, parent.getHeight(), paint);
            }
        });
        binding.recyclerView.setAdapter(liveListAdapter = new LiveListAdapter());
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

        private List<LiveList.LiveListItem> data;

        @NonNull
        @Override
        public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ItemViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder viewHolder, int position) {
            if (viewHolder instanceof ItemViewHolder) {
                ((ItemViewHolder) viewHolder).onBindViewHolder(position, data.get(position));
            }
        }

        @Override
        public int getItemCount() {
            if (data == null)
                return 0;
            if (data.size() > 12) {
                //return 12;//测试ScrollEvent
            }
            return data.size();
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
            binding.txtContent.setText(String.format(Locale.getDefault(),
                    "%s-%d", item.desc, position));
            binding.getRoot().setOnClickListener(v -> {
                WebActivityStarter.create(Config.instance().homePage)
                        .start(LiveListFeature.this);
            });
        }
    }
}

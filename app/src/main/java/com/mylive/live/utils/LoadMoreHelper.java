package com.mylive.live.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

/**
 * Create by zailongshi on 2019/8/3
 */
public final class LoadMoreHelper {

    private boolean isStrictMode, loading;
    private RecyclerView recyclerView;
    private OnLoadMoreListener onLoadMoreListener;

    public static LoadMoreHelper create(RecyclerView recyclerView, boolean isStrictMode) {
        return new LoadMoreHelper(recyclerView, isStrictMode);
    }

    private LoadMoreHelper(RecyclerView recyclerView, boolean isStrictMode) {
        Objects.requireNonNull(recyclerView);
        this.recyclerView = recyclerView;
        this.isStrictMode = isStrictMode;
        setupScrollListener();
    }

    private void setupScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView,
                                             int newState) {
                if (!loading && onLoadMoreListener != null) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (recyclerView.getChildCount() > 0) {
                            View child = recyclerView.getChildAt(
                                    recyclerView.getChildCount() - 1);
                            int position = recyclerView.getChildAdapterPosition(child);
                            RecyclerView.Adapter adapter = recyclerView.getAdapter();
                            if (adapter != null && position == adapter.getItemCount() - 1) {
                                if (isStrictMode) {
                                    loading = true;
                                }
                                onLoadMoreListener.onLoadMore();
                            }
                        }
                    }
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}

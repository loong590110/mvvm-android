package com.mylive.live.base;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Developer Zailong Shi on 2020/5/21.
 */
public class BindingViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {
    protected T binding;

    @Deprecated//IDE辅助
    protected BindingViewHolder(@NonNull ViewGroup parent) {
        super(parent);
    }

    protected BindingViewHolder(@NonNull ViewGroup parent, @LayoutRes int layoutResId) {
        this(DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), layoutResId, parent, false
        ));
    }

    protected BindingViewHolder(T binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}

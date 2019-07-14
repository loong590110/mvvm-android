package com.mylive.live.base;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Create by zailongshi on 2019/7/8
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {
    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    /**
     * 辅助IDE使用
     *
     * @param parent
     */
    @Deprecated
    public BaseViewHolder(@NonNull ViewGroup parent) {
        this(parent, android.R.layout.simple_list_item_1);
    }

    public BaseViewHolder(@NonNull ViewGroup parent, @LayoutRes int layoutResId) {
        super(LayoutInflater.from(parent.getContext()).inflate(
                layoutResId, parent, false));
    }
}

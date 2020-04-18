package com.mylive.live.view.room;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mylive.live.R;
import com.mylive.live.databinding.FragmentGiftsBinding;
import com.mylive.live.recyclerview.PagingScrollHelper;

/**
 * Created by Developer Zailong Shi on 2020/4/18.
 */
public class GiftsFragment extends Fragment {

    private FragmentGiftsBinding binding;

    public static GiftsFragment newInstance() {
        return new GiftsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return (binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_gifts, container, false
        )).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        new PagingScrollHelper(4 * 2).attachToRecyclerView(binding.recyclerView);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(
                getContext(), 2, GridLayoutManager.HORIZONTAL, false
        ));
        final int screenWidth = getResources().getDisplayMetrics().widthPixels;
        binding.recyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                TextView textView = new TextView(parent.getContext());
                textView.setLayoutParams(new ViewGroup.LayoutParams(
                        screenWidth / 4, ViewGroup.LayoutParams.MATCH_PARENT
                ));
                textView.setBackgroundColor(0xff445566);
                return new RecyclerView.ViewHolder(textView) {
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((TextView) holder.itemView).setText("gift " + (position + 1));
            }

            @Override
            public int getItemCount() {
                return 24;
            }
        });
    }
}

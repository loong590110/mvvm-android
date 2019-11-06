package com.mylive.live.widget;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Developer Zailong Shi on 2019-11-04.
 */
public class SpannedGridLayoutManager extends RecyclerView.LayoutManager {

    public static final int HORIZONTAL = RecyclerView.HORIZONTAL;
    public static final int VERTICAL = RecyclerView.VERTICAL;

    private int spanCount;
    private int itemRatio;
    private int orientation;

    private EdgeInfo[] edgeInfos;

    public SpannedGridLayoutManager(int spanCount) {
        this(spanCount, 1, VERTICAL);
    }

    public SpannedGridLayoutManager(int spanCount, int itemRatio) {
        this(spanCount, itemRatio, VERTICAL);
    }

    public SpannedGridLayoutManager(int spanCount, int itemRatio, int orientation) {
        this.spanCount = spanCount;
        this.itemRatio = itemRatio;
        this.orientation = orientation;
        this.edgeInfos = new EdgeInfo[spanCount];
    }

    @Nullable
    @Override
    public Parcelable onSaveInstanceState() {
        return new SaveState(edgeInfos);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SaveState saveState = (SaveState) state;
        edgeInfos = saveState.edgeInfos;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        for (int i = 0; i < edgeInfos.length; i++) {
            EdgeInfo edgeInfo = edgeInfos[i];
            if (edgeInfo == null) {
                edgeInfo = edgeInfos[i] = new EdgeInfo();
            }
        }
    }

    private static class EdgeInfo implements Parcelable {

        private int adapterPosition;
        private int startPosition;
        private int endPosition;
        private int startLocation;
        private int endLocation;

        EdgeInfo() {
        }

        protected EdgeInfo(Parcel in) {
            adapterPosition = in.readInt();
            startPosition = in.readInt();
            endPosition = in.readInt();
            startLocation = in.readInt();
            endLocation = in.readInt();
        }

        public static final Creator<EdgeInfo> CREATOR = new Creator<EdgeInfo>() {
            @Override
            public EdgeInfo createFromParcel(Parcel in) {
                return new EdgeInfo(in);
            }

            @Override
            public EdgeInfo[] newArray(int size) {
                return new EdgeInfo[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(adapterPosition);
            dest.writeInt(startPosition);
            dest.writeInt(endPosition);
            dest.writeInt(startLocation);
            dest.writeInt(endLocation);
        }
    }

    private static class SaveState implements Parcelable {

        private EdgeInfo[] edgeInfos;

        SaveState(EdgeInfo[] edgeInfos) {
            this.edgeInfos = edgeInfos;
        }

        protected SaveState(Parcel in) {
            edgeInfos = (EdgeInfo[]) in.readParcelableArray(in.getClass().getClassLoader());
        }

        public static final Creator<SaveState> CREATOR = new Creator<SaveState>() {
            @Override
            public SaveState createFromParcel(Parcel in) {
                return new SaveState(in);
            }

            @Override
            public SaveState[] newArray(int size) {
                return new SaveState[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelableArray(edgeInfos, flags);
        }
    }
}

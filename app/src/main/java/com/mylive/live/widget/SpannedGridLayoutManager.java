package com.mylive.live.widget;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Developer Zailong Shi on 2019-11-04.
 */
public class SpannedGridLayoutManager extends RecyclerView.LayoutManager implements
        RecyclerView.SmoothScroller.ScrollVectorProvider {

    public static final int HORIZONTAL = RecyclerView.HORIZONTAL;
    public static final int VERTICAL = RecyclerView.VERTICAL;

    private int spanCount;
    private int itemRatio;
    private int orientation;
    private SpanSizeLookup spanSizeLookup;

    private SaveState state;

    public SpannedGridLayoutManager(int spanCount) {
        this(spanCount, 1, VERTICAL);
    }

    public SpannedGridLayoutManager(int spanCount, int itemRatio) {
        this(spanCount, itemRatio, VERTICAL);
    }

    public SpannedGridLayoutManager(int spanCount, int itemRatio, int orientation) {
        if (spanCount < 1) {
            throw new IllegalArgumentException("spanCount不能少于1。");
        }
        this.spanCount = spanCount;
        this.itemRatio = itemRatio;
        this.orientation = orientation;
        this.state = new SaveState(new EdgeInfo[spanCount]);
    }

    @Nullable
    @Override
    public Parcelable onSaveInstanceState() {
        return state;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        this.state = (SaveState) state;
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
        detachAndScrapAttachedViews(recycler);
        for (int i = 0; i < this.state.edgeInfos.length; i++) {
            EdgeInfo edgeInfo = this.state.edgeInfos[i];
            if (edgeInfo == null) {
                edgeInfo = this.state.edgeInfos[i] = new EdgeInfo();
            }
        }
    }

    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {

    }

    public int getSpanCount() {
        return spanCount;
    }

    public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {
        this.spanSizeLookup = spanSizeLookup;
    }

    @Nullable
    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        return null;
    }

    public static abstract class SpanSizeLookup {
        public abstract SpanSize getSpanSize(int position);
    }

    public static class SpanSize {
        private int columnSpan;
        private int rowSpan;

        public SpanSize(int columnSpan, int rowSpan) {
            this.columnSpan = columnSpan;
            this.rowSpan = rowSpan;
        }

        public int getColumnSpan() {
            return columnSpan;
        }

        public void setColumnSpan(int columnSpan) {
            this.columnSpan = columnSpan;
        }

        public int getRowSpan() {
            return rowSpan;
        }

        public void setRowSpan(int rowSpan) {
            this.rowSpan = rowSpan;
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

        private int pendingScrollPosition = RecyclerView.NO_POSITION;
        private EdgeInfo[] edgeInfos;

        SaveState(EdgeInfo[] edgeInfos) {
            this.edgeInfos = edgeInfos;
            for (int i = 0; i < this.edgeInfos.length; i++) {
                this.edgeInfos[i] = new EdgeInfo();
            }
        }

        protected SaveState(Parcel in) {
            pendingScrollPosition = in.readInt();
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
            dest.writeInt(pendingScrollPosition);
            dest.writeParcelableArray(edgeInfos, flags);
        }
    }
}

package com.mylive.live.widget;

import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

/**
 * Created by Developer Zailong Shi on 2019-10-16.
 */
public class PagingScrollHelper extends SnapHelper {

    private static final int MAX_SCROLL_ON_FLING_DURATION = 100; // ms
    private static final float MILLISECONDS_PER_INCH = 100f;

    @Nullable
    private OrientationHelper mVerticalHelper;
    @Nullable
    private OrientationHelper mHorizontalHelper;

    private RecyclerView mRecyclerView;
    private int mPageSize, mCurrentPageIndex;
    private boolean scrollByFling;

    public PagingScrollHelper(int pageSize) {
        this.mPageSize = pageSize;
    }

    public void setCurrentPageIndex(int currentPageIndex) {
        this.mCurrentPageIndex = currentPageIndex;
        int position = currentPageIndex * mPageSize;
        if (mRecyclerView == null || mRecyclerView.getLayoutManager() == null) {
            return;
        }
        RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForAdapterPosition(position);
        if (viewHolder == null) {
            return;
        }
        int[] snapDistance = calculateDistanceToFinalSnap(
                mRecyclerView.getLayoutManager(),
                viewHolder.itemView
        );
        if (snapDistance == null) {
            return;
        }
        if (snapDistance[0] != 0 || snapDistance[1] != 0) {
            mRecyclerView.smoothScrollBy(snapDistance[0], snapDistance[1]);
        }
    }

    public int getPageSize() {
        return mPageSize;
    }

    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView)
            throws IllegalStateException {
        this.mRecyclerView = recyclerView;
        super.attachToRecyclerView(recyclerView);
    }

    @Nullable
    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
                                              @NonNull View targetView) {
        int[] out = new int[2];
        if (layoutManager.canScrollHorizontally()) {
            out[0] = distanceToTop(
                    layoutManager,
                    targetView,
                    getHorizontalHelper(
                            layoutManager
                    )
            );
        } else {
            out[0] = 0;
        }

        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToTop(
                    layoutManager,
                    targetView,
                    getVerticalHelper(
                            layoutManager
                    )
            );
        } else {
            out[1] = 0;
        }
        return out;
    }

    @Nullable
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.canScrollVertically()) {
            return findTargetView(layoutManager, getVerticalHelper(layoutManager));
        } else if (layoutManager.canScrollHorizontally()) {
            return findTargetView(layoutManager, getHorizontalHelper(layoutManager));
        }
        return null;
    }

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager,
                                      int velocityX, int velocityY) {
        final int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION;
        }

        final boolean forwardDirection;
        if (layoutManager.canScrollHorizontally()) {
            forwardDirection = velocityX > 0;
        } else {
            forwardDirection = velocityY > 0;
        }
        boolean reverseLayout = false;
        if ((layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            RecyclerView.SmoothScroller.ScrollVectorProvider vectorProvider =
                    (RecyclerView.SmoothScroller.ScrollVectorProvider) layoutManager;
            PointF vectorForEnd = vectorProvider.computeScrollVectorForPosition(itemCount - 1);
            if (vectorForEnd != null) {
                reverseLayout = vectorForEnd.x < 0 || vectorForEnd.y < 0;
            }
        }
        mCurrentPageIndex = !reverseLayout
                ?
                forwardDirection ? mCurrentPageIndex + 1 : mCurrentPageIndex - 1
                :
                forwardDirection ? mCurrentPageIndex - 1 : mCurrentPageIndex + 1;
        final int targetPosition = mCurrentPageIndex * mPageSize;
        scrollByFling = true;
        return targetPosition;
    }

    @Override
    protected LinearSmoothScroller createScroller(RecyclerView.LayoutManager layoutManager) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return null;
        }
        return new LinearSmoothScroller(mRecyclerView.getContext()) {
            @Override
            protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
                int[] snapDistances = calculateDistanceToFinalSnap(mRecyclerView.getLayoutManager(),
                        targetView);
                final int dx = snapDistances[0];
                final int dy = snapDistances[1];
                final int time = calculateTimeForDeceleration(Math.max(Math.abs(dx), Math.abs(dy)));
                if (time > 0) {
                    action.update(dx, dy, time, mDecelerateInterpolator);
                }
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
            }

            @Override
            protected int calculateTimeForScrolling(int dx) {
                return Math.min(MAX_SCROLL_ON_FLING_DURATION, super.calculateTimeForScrolling(dx));
            }
        };
    }

    private int distanceToTop(@NonNull RecyclerView.LayoutManager layoutManager,
                              @NonNull View targetView, OrientationHelper helper) {
        return helper.getDecoratedStart(targetView);
    }

    @Nullable
    private View findTargetView(RecyclerView.LayoutManager layoutManager,
                                OrientationHelper helper) {
        if (scrollByFling) {
            scrollByFling = false;
            return null;
        }

        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }

        //region: 处理最后一页不满页的情况，判断拖拽到底部则认为滚到最后一页
        View lastChild = layoutManager.getChildAt(childCount - 1);
        if (lastChild == null) {
            return null;
        }
        int lastPosition = layoutManager.getPosition(lastChild);
        if (lastPosition == layoutManager.getItemCount() - 1) {
            if (helper.getDecoratedEnd(lastChild) <= helper.getTotalSpace()) {
                mCurrentPageIndex = lastPosition / mPageSize;
                return lastChild;
            }
        }
        //endregion

        View closestChild = layoutManager.getChildAt(0);
        if (closestChild == null) {
            return null;
        }

        int position = layoutManager.getPosition(closestChild);
        int smallerPosition = position / mPageSize * mPageSize;
        int largerPosition = smallerPosition + mPageSize;
        int targetPosition = largerPosition - position > position - smallerPosition ?
                smallerPosition : largerPosition;
        mCurrentPageIndex = targetPosition / mPageSize;
        closestChild = layoutManager.findViewByPosition(targetPosition);
        return closestChild;
    }

    @NonNull
    private OrientationHelper getVerticalHelper(@NonNull RecyclerView.LayoutManager layoutManager) {
        if (mVerticalHelper == null || mVerticalHelper.getLayoutManager() != layoutManager) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return mVerticalHelper;
    }

    @NonNull
    private OrientationHelper getHorizontalHelper(
            @NonNull RecyclerView.LayoutManager layoutManager) {
        if (mHorizontalHelper == null || mHorizontalHelper.getLayoutManager() != layoutManager) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return mHorizontalHelper;
    }
}

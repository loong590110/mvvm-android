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

    private static final int MAX_SCROLL_ON_FLING_DURATION = 100; //default: 100 ms
    private static final float MILLISECONDS_PER_INCH = 25f;//default:100f;

    @Nullable
    private OrientationHelper mVerticalHelper;
    @Nullable
    private OrientationHelper mHorizontalHelper;

    private LinearSmoothScroller smoothScroller;
    private RecyclerView mRecyclerView;
    private int mPageSize, mCurrentPageIndex;
    private boolean scrollByFling;
    private PositionConverter positionConverter;

    public interface PositionConverter {
        int getActualPosition(RecyclerView recyclerView, int virtualPosition);

        int getVirtualPosition(RecyclerView recyclerView, int actualPosition);
    }

    public PagingScrollHelper(int pageSize) {
        this(pageSize, null);
    }

    public PagingScrollHelper(int pageSize, PositionConverter converter) {
        this.mPageSize = pageSize;
        this.positionConverter = converter;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public PositionConverter getPositionConverter() {
        return positionConverter;
    }

    public void setSelectedPosition(int position) {
        //被选中礼物的区域没有完全显示的情况下切换到下一页，确保礼物被完全显示
        RecyclerView.ViewHolder viewHolder =
                mRecyclerView.findViewHolderForAdapterPosition(position);
        if (viewHolder == null || viewHolder.itemView == null) {
            return;
        }
        if (mVerticalHelper.getDecoratedEnd(viewHolder.itemView)
                > mVerticalHelper.getTotalSpace()) {
            setCurrentPageIndex(getCurrentPageIndex() + 1);
        } else if (mVerticalHelper.getDecoratedStart(viewHolder.itemView)
                < mVerticalHelper.getStartAfterPadding()) {
            setCurrentPageIndex(getCurrentPageIndex() - 1);
        }
    }

    public int getCurrentPageIndex() {
        return mCurrentPageIndex;
    }

    public void setCurrentPageIndex(int currentPageIndex) {
        this.mCurrentPageIndex = currentPageIndex;
        checkCurrentPageIndex();
        int position = convertActual(currentPageIndex * mPageSize);
        if (mRecyclerView == null || mRecyclerView.getLayoutManager() == null) {
            return;
        }
        RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForAdapterPosition(position);
        if (viewHolder == null) {
            scrollToTargetPosition(position);
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
        if (recyclerView == null) {
            throw new NullPointerException();
        }
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
        checkCurrentPageIndex();
        final int targetPosition = convertActual(mCurrentPageIndex * mPageSize);
        scrollByFling = true;
        return targetPosition;
    }

    @Override
    protected LinearSmoothScroller createScroller(RecyclerView.LayoutManager layoutManager) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return null;
        }
        if (smoothScroller == null) {
            smoothScroller = new LinearSmoothScroller(mRecyclerView.getContext()) {
                @Override
                protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
                    int[] snapDistances = calculateDistanceToFinalSnap(mRecyclerView.getLayoutManager(),
                            targetView);
                    final int dx = snapDistances[0];
                    final int dy = snapDistances[1];
                    final int time = calculateTimeForDeceleration(Math.max(Math.abs(dx), Math.abs(dy)));
                    if (time > 0) {
                        action.update(0, 0, time, mDecelerateInterpolator);
                    }
                }

                @Override
                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                    return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                }

                @Override
                protected int calculateTimeForScrolling(int dx) {
//          return Math.min(MAX_SCROLL_ON_FLING_DURATION, super.calculateTimeForScrolling(dx));
                    return super.calculateTimeForScrolling(dx);
                }
            };
        }
        return smoothScroller;
    }

    private int distanceToTop(@NonNull RecyclerView.LayoutManager layoutManager,
                              @NonNull View targetView, OrientationHelper helper) {
        return helper.getDecoratedStart(targetView);
    }

    @Nullable
    private View findTargetView(RecyclerView.LayoutManager layoutManager,
                                OrientationHelper helper) {
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }

        //region: 处理最后一页不满页的情况，判断拖拽到底部则认为滚到最后一页
        View lastChild = layoutManager.getChildAt(childCount - 1);
        int lastPosition = layoutManager.getPosition(lastChild);
        if (lastPosition == layoutManager.getItemCount() - 1) {
            if (helper.getDecoratedEnd(lastChild) <= helper.getTotalSpace()) {
                mCurrentPageIndex = lastPosition / mPageSize;
                scrollByFling = false;
                return lastChild;
            }
        }
        //endregion

        View closestChild = null;
        for (int i = 0; i < childCount; i++) {
            closestChild = layoutManager.getChildAt(i);
            if (closestChild != null) {
                if (helper.getDecoratedEnd(closestChild) > helper.getStartAfterPadding()) {
                    break;
                }
            }
        }

        if (closestChild == null) {
            return null;
        }

        int position = layoutManager.getPosition(closestChild);
        if (scrollByFling) {
            //保障滚动到指定页面
            int pageIndex = convertVirtual(position) / mPageSize;
            if (pageIndex == mCurrentPageIndex) {
                scrollByFling = false;
            }
        } else {
            int smallerPosition = convertVirtual(position) / mPageSize * mPageSize;
            int largerPosition = smallerPosition + mPageSize;
            int virtualPosition = largerPosition - position > position - smallerPosition ?
                    smallerPosition : largerPosition;
            mCurrentPageIndex = virtualPosition / mPageSize;
            checkCurrentPageIndex();
        }
        int actualPosition = convertActual(mCurrentPageIndex * mPageSize);
        View targetChild = layoutManager.findViewByPosition(actualPosition);
        if (targetChild == null) {
            scrollToTargetPosition(actualPosition);
            return null;
        }
        return targetChild;
    }

    private void checkCurrentPageIndex() {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (mCurrentPageIndex < 0) {
            mCurrentPageIndex = 0;
        } else if (mCurrentPageIndex * mPageSize >= convertVirtual(layoutManager.getItemCount())) {
            mCurrentPageIndex -= 1;
        }
    }

    private int convertActual(int position) {
        if (positionConverter != null) {
            return positionConverter.getActualPosition(
                    mRecyclerView,
                    position
            );
        }
        return position;
    }

    private int convertVirtual(int position) {
        if (positionConverter != null) {
            return positionConverter.getVirtualPosition(
                    mRecyclerView,
                    position
            );
        }
        return position;
    }

    private void scrollToTargetPosition(int position) {
        scrollByFling = true;
        mRecyclerView.smoothScrollToPosition(position);
    }

    @NonNull
    private OrientationHelper getVerticalHelper(@NonNull RecyclerView.LayoutManager
                                                        layoutManager) {
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

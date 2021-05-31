package ir.afraapps.gviews.scroll;


import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class AdvancedRecyclerView extends RecyclerView {
  private int mScrollY;
  private RecyclerViewCallbacks mCallbacks;
  private LinearLayoutManager mLayoutManager;
  private GridLayoutManager mGridLayoutManager;
  private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
  private int mTotalItemCount;
  private int mLastVisibleItem;
  private int firstCompleteVisibleItem;
  private int[] positions = new int[8];
  private int visibleThreshold = 4;
  private boolean loading;

  public AdvancedRecyclerView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public AdvancedRecyclerView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AdvancedRecyclerView(Context context) {
    super(context);
  }

  @Override
  public void onRestoreInstanceState(Parcelable state) {
    if (state instanceof Bundle) {
      Bundle bundle = (Bundle) state;
      this.mTotalItemCount = bundle.getInt("totalItemCount");
      this.mLastVisibleItem = bundle.getInt("lastVisibleItem");
      this.mScrollY = bundle.getInt("scrollY");
      state = bundle.getParcelable("superState");
    }

    super.onRestoreInstanceState(state);
  }

  @Override
  public Parcelable onSaveInstanceState() {
    Bundle bundle = new Bundle();
    bundle.putParcelable("superState", super.onSaveInstanceState());
    bundle.putInt("totalItemCount", this.mTotalItemCount);
    bundle.putInt("lastVisibleItem", this.mLastVisibleItem);
    bundle.putInt("scrollY", this.mScrollY);
    return bundle;
  }

  @Override
  public void onScrolled(int dx, int dy) {
    super.onScrolled(dx, dy);
    if (this.mCallbacks != null) {
      if (this.mGridLayoutManager != null) {
        this.mTotalItemCount = this.mGridLayoutManager.getItemCount();
        this.mLastVisibleItem = this.mGridLayoutManager.findLastVisibleItemPosition();
        this.firstCompleteVisibleItem = this.mGridLayoutManager.findFirstCompletelyVisibleItemPosition();
      } else if (this.mLayoutManager != null) {
        this.mTotalItemCount = this.mLayoutManager.getItemCount();
        this.mLastVisibleItem = this.mLayoutManager.findLastVisibleItemPosition();
        this.firstCompleteVisibleItem = this.mLayoutManager.findFirstCompletelyVisibleItemPosition();
      } else if (this.mStaggeredGridLayoutManager != null) {
        this.mTotalItemCount = this.mStaggeredGridLayoutManager.getItemCount();
        this.mLastVisibleItem = this.mStaggeredGridLayoutManager.findLastVisibleItemPositions(this.positions)[0];
        this.firstCompleteVisibleItem = this.mStaggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(this.positions)[0];
      }

      this.mScrollY += dy;
      if (this.firstCompleteVisibleItem == 0) {
        this.mScrollY = 0;
      }

      this.mCallbacks.onScrollChanged(this.mScrollY);
      if (!this.loading && this.mTotalItemCount <= this.mLastVisibleItem + this.visibleThreshold) {
        Log.i("RecyclerViewLog", "totalItemCount: " + this.mTotalItemCount + " | lastVisibleItem: " + this.mLastVisibleItem);
        this.loading = true;
        this.mCallbacks.onLoadMore();
      }
    }

  }

  public int findFirstVisibleItemPosition() {
    if (this.mGridLayoutManager != null) {
      return this.mGridLayoutManager.findFirstVisibleItemPosition();
    } else if (this.mLayoutManager != null) {
      return this.mLayoutManager.findFirstVisibleItemPosition();
    } else {
      return this.mStaggeredGridLayoutManager != null ? this.mStaggeredGridLayoutManager.findFirstVisibleItemPositions(this.positions)[0] : -1;
    }
  }

  public View findViewByPosition(int position) {
    if (this.mGridLayoutManager != null) {
      return this.mGridLayoutManager.findViewByPosition(position);
    } else if (this.mLayoutManager != null) {
      return this.mLayoutManager.findViewByPosition(position);
    } else {
      return this.mStaggeredGridLayoutManager != null ? this.mStaggeredGridLayoutManager.findViewByPosition(position) : null;
    }
  }

  public void setVisibleThreshold(int threshold) {
    this.visibleThreshold = threshold;
  }

  public void setLoading(boolean loading) {
    this.loading = loading;
  }

  public void setScrollViewCallbacks(RecyclerViewCallbacks listener) {
    this.mCallbacks = listener;
  }

  public int getCurrentScrollY() {
    return this.mScrollY;
  }

  public void setCurrentScrollY(int y) {
    this.mScrollY = y;
  }

  public void scrollVerticallyTo(int y) {
    View firstVisibleChild = this.getChildAt(0);
    if (firstVisibleChild != null) {
      int baseHeight = firstVisibleChild.getHeight();
      int position = y / baseHeight;
      this.scrollVerticallyToPosition(position);
    }

  }

  public void scrollVerticallyToPosition(int position) {
    if (this.mGridLayoutManager != null) {
      this.mGridLayoutManager.scrollToPositionWithOffset(position, 0);
    } else if (this.mLayoutManager != null) {
      this.mLayoutManager.scrollToPositionWithOffset(position, 0);
    } else if (this.mStaggeredGridLayoutManager != null) {
      this.mStaggeredGridLayoutManager.scrollToPositionWithOffset(position, 0);
    } else {
      this.scrollToPosition(position);
    }

  }

  public void scrollToY(int y) {
    if (this.mGridLayoutManager != null) {
      this.mGridLayoutManager.scrollToPositionWithOffset(0, -y);
    } else if (this.mLayoutManager != null) {
      this.mLayoutManager.scrollToPositionWithOffset(0, -y);
    } else if (this.mStaggeredGridLayoutManager != null) {
      this.mStaggeredGridLayoutManager.scrollToPositionWithOffset(0, -y);
    } else {
      this.scrollBy(0, y);
    }

  }

  public int getCurrentPostion() {
    if (this.mGridLayoutManager != null) {
      return this.mGridLayoutManager.findFirstVisibleItemPosition();
    } else if (this.mLayoutManager != null) {
      return this.mLayoutManager.findFirstVisibleItemPosition();
    } else {
      return this.mStaggeredGridLayoutManager != null ? this.mStaggeredGridLayoutManager.findFirstVisibleItemPositions(this.positions)[0] : this.mScrollY;
    }
  }

  @Override
  public LayoutManager getLayoutManager() {
    if (this.mGridLayoutManager != null) {
      return this.mGridLayoutManager;
    } else {
      return this.mLayoutManager != null ? this.mLayoutManager : super.getLayoutManager();
    }
  }

  @Override
  public void setLayoutManager(LayoutManager layout) {
    super.setLayoutManager(layout);
    if (layout instanceof GridLayoutManager) {
      this.mGridLayoutManager = (GridLayoutManager) layout;
    } else if (layout instanceof LinearLayoutManager) {
      this.mLayoutManager = (LinearLayoutManager) layout;
    } else if (layout instanceof StaggeredGridLayoutManager) {
      this.mStaggeredGridLayoutManager = (StaggeredGridLayoutManager) layout;
    }

  }

  @Override
  protected void attachLayoutAnimationParameters(View child, ViewGroup.LayoutParams params, int index, int count) {
    LayoutManager layoutManager = this.getLayoutManager();
    if (this.getAdapter() != null && layoutManager instanceof GridLayoutManager) {
      GridLayoutAnimationController.AnimationParameters animationParams = (GridLayoutAnimationController.AnimationParameters) params.layoutAnimationParameters;
      if (animationParams == null) {
        animationParams = new GridLayoutAnimationController.AnimationParameters();
        params.layoutAnimationParameters = animationParams;
      }

      animationParams.count = count;
      animationParams.index = index;
      int columns = ((GridLayoutManager) layoutManager).getSpanCount();
      animationParams.columnsCount = columns;
      animationParams.rowsCount = count / columns;
      int invertedIndex = count - 1 - index;
      animationParams.column = columns - 1 - invertedIndex % columns;
      animationParams.row = animationParams.rowsCount - 1 - invertedIndex / columns;
    } else {
      super.attachLayoutAnimationParameters(child, params, index, count);
    }
  }

}

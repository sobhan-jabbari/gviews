package ir.afraapps.gviews.scroll;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


public class HeaderRecyclerView extends RecyclerView {
  static final int MIN_DISTANCE = 30;
  int offset;
  private int mScrollY;
  private HeaderScrollCallbacks mCallbacks;
  private LinearLayoutManager mLayoutManager;
  private GridLayoutManager mGridLayoutManager;
  private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
  private int totalItemCount;
  private int lastVisibleItem;
  private int firstVisibleItem;
  private int firstCompleteVisibleItem;
  private int[] positions;
  private int visibleThreshold = 1;
  private boolean loading;
  private int flingSpeed = 1;
  private int headerHeight;
  private int headerMiniHeight;
  private int toolbarHeight;
  private int toolbarHeightHalf;
  private boolean isInFling;
  private boolean isVisibleFabGoUp;
  private int defaultItemCount = 1;
  private float oldX;
  private Runnable flingHandler;

  public HeaderRecyclerView(Context context) {
    super(context);
    this.flingHandler = () -> isInFling = false;
  }

  public HeaderRecyclerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.flingHandler = () -> isInFling = false;
  }

  public HeaderRecyclerView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    this.flingHandler = () -> isInFling = false;
  }

  @Override
  public void onRestoreInstanceState(Parcelable state) {
    if (state instanceof Bundle) {
      Bundle bundle = (Bundle) state;
      this.totalItemCount = bundle.getInt("totalItemCount");
      this.lastVisibleItem = bundle.getInt("lastVisibleItem");
      this.mScrollY = bundle.getInt("scrollY");
      this.offset = bundle.getInt("offset");
      state = bundle.getParcelable("superState");
    }

    super.onRestoreInstanceState(state);
  }

  @Override
  public Parcelable onSaveInstanceState() {
    Bundle bundle = new Bundle();
    bundle.putParcelable("superState", super.onSaveInstanceState());
    bundle.putInt("totalItemCount", this.totalItemCount);
    bundle.putInt("lastVisibleItem", this.lastVisibleItem);
    bundle.putInt("scrollY", this.mScrollY);
    bundle.putInt("offset", this.offset);
    return bundle;
  }

  public void setFlingSpeed(int flingSpeed) {
    this.flingSpeed = flingSpeed;
  }

  @Override
  public boolean fling(int velocityX, int velocityY) {
    velocityY *= this.flingSpeed;
    this.isInFling = true;
    this.postDelayed(this.flingHandler, 200L);
    return super.fling(velocityX, velocityY);
  }

  @Override
  public void onScrollStateChanged(int state) {
    super.onScrollStateChanged(state);
    if (state == 0) {
      if (this.isInFling && this.mScrollY < 0) {
        this.mScrollY = 0;
        if (this.mCallbacks != null) {
          this.mCallbacks.onScrollChanged(this.mScrollY);
        }
      }

      if (this.mScrollY < this.headerHeight || this.offset > 0 && this.offset < this.toolbarHeightHalf) {
        this.offset = 0;
        if (this.mCallbacks != null) {
          this.mCallbacks.onShow();
        }
      } else if (this.mScrollY > this.headerHeight && this.offset < this.toolbarHeight && this.offset >= this.toolbarHeightHalf) {
        this.offset = this.toolbarHeight;
        if (this.mCallbacks != null) {
          this.mCallbacks.onHide();
        }
      }
    }

  }

  @Override
  public void onScrolled(int dx, int dy) {
    super.onScrolled(dx, dy);
    this.detectPositions();
    if (dy > 0) {
      if (this.isVisibleFabGoUp) {
        this.isVisibleFabGoUp = false;
        if (this.mCallbacks != null) {
          this.mCallbacks.hideFabGoUp();
        }
      }
    } else if (dy < 0) {
      if (this.totalItemCount > 15 && this.firstVisibleItem > 15) {
        if (!this.isVisibleFabGoUp) {
          this.isVisibleFabGoUp = true;
          if (this.mCallbacks != null) {
            this.mCallbacks.showFabGoUp();
          }
        }
      } else if (this.firstVisibleItem < this.defaultItemCount && this.isVisibleFabGoUp) {
        this.isVisibleFabGoUp = false;
        if (this.mCallbacks != null) {
          this.mCallbacks.hideFabGoUp();
        }
      }
    }

    this.mScrollY += dy;
    if (this.firstCompleteVisibleItem == 0) {
      this.mScrollY = 0;
      if (this.mCallbacks != null) {
        this.mCallbacks.onFirst();
      }
    }

    if (this.mCallbacks != null) {
      this.mCallbacks.onScrollChanged(this.mScrollY);
    }

    if (dy < 0 || this.mScrollY > this.headerMiniHeight) {
      this.offset += dy;
      this.clipOffset();
      if (this.mCallbacks != null) {
        this.mCallbacks.onOffsetChanged(this.offset);
      }
    }

    if (!this.loading && this.totalItemCount > this.defaultItemCount && this.totalItemCount <= this.lastVisibleItem + this.visibleThreshold) {
      this.loading = true;
      if (this.mCallbacks != null) {
        this.mCallbacks.onLoadMore();
      }
    }

  }

  private void detectPositions() {
    if (this.mGridLayoutManager != null) {
      this.totalItemCount = this.mGridLayoutManager.getItemCount();
      this.lastVisibleItem = this.mGridLayoutManager.findLastVisibleItemPosition();
      this.firstCompleteVisibleItem = this.mGridLayoutManager.findFirstCompletelyVisibleItemPosition();
      this.firstVisibleItem = this.mGridLayoutManager.findFirstVisibleItemPosition();
    } else if (this.mLayoutManager != null) {
      this.totalItemCount = this.mLayoutManager.getItemCount();
      this.lastVisibleItem = this.mLayoutManager.findLastVisibleItemPosition();
      this.firstCompleteVisibleItem = this.mLayoutManager.findFirstCompletelyVisibleItemPosition();
      this.firstVisibleItem = this.mLayoutManager.findFirstVisibleItemPosition();
    } else if (this.mStaggeredGridLayoutManager != null) {
      this.totalItemCount = this.mStaggeredGridLayoutManager.getItemCount();
      this.lastVisibleItem = this.mStaggeredGridLayoutManager.findLastVisibleItemPositions(this.positions)[0];
      this.firstCompleteVisibleItem = this.mStaggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(this.positions)[0];
      this.firstVisibleItem = this.mStaggeredGridLayoutManager.findFirstVisibleItemPositions(this.positions)[0];
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

  public void setSpanCount(int spanCount) {
    if (this.mStaggeredGridLayoutManager != null) {
      this.positions = new int[spanCount];
    }

  }

  private void clipOffset() {
    if (this.offset < 0) {
      this.offset = 0;
    } else if (this.offset > this.toolbarHeight) {
      this.offset = this.toolbarHeight;
    }

  }

  public void resetScrollOffset() {
    this.offset = 0;
  }

  public void resetHeaderScroll() {
    this.offset = 0;
    if (this.mCallbacks != null) {
      this.mCallbacks.onShow();
    }

  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
  }

  public void setHeaderItemsHeight(int headerHeight, int toolbarHeight, int headerMiniHeight) {
    this.toolbarHeight = toolbarHeight;
    this.toolbarHeightHalf = toolbarHeight / 2;
    this.headerHeight = headerHeight;
    this.headerMiniHeight = headerMiniHeight;
  }

  public void setDefaultItemCount(int defaultItemCount) {
    this.defaultItemCount = defaultItemCount;
  }

  @Override
  @SuppressLint({"ClickableViewAccessibility"})
  public boolean onTouchEvent(MotionEvent e) {
    float height = (float) (this.headerHeight - this.mScrollY);
    return e.getY() >= height && super.onTouchEvent(e);
  }

  public void setScrollViewCallbacks(HeaderScrollCallbacks listener) {
    this.mCallbacks = listener;
  }

  public void scrollToY(int y) {
    if (this.mLayoutManager != null) {
      this.mLayoutManager.scrollToPositionWithOffset(0, -y);
    } else if (this.mGridLayoutManager != null) {
      this.mGridLayoutManager.scrollToPositionWithOffset(0, -y);
    } else if (this.mStaggeredGridLayoutManager != null) {
      this.mStaggeredGridLayoutManager.scrollToPositionWithOffset(0, -y);
    } else {
      this.scrollTo(0, y);
    }

  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
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
      this.positions = new int[this.mStaggeredGridLayoutManager.getSpanCount()];
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

  public void setVisibleThreshold(int threshold) {
    this.visibleThreshold = threshold;
  }

  public void setLoading(boolean loading) {
    this.loading = loading;
  }

  public void scrollVerticallyToPosition(int position) {
    if (this.mLayoutManager != null) {
      this.mLayoutManager.scrollToPositionWithOffset(position, 0);
    } else if (this.mGridLayoutManager != null) {
      this.mGridLayoutManager.scrollToPositionWithOffset(position, 0);
    } else if (this.mStaggeredGridLayoutManager != null) {
      this.mStaggeredGridLayoutManager.scrollToPositionWithOffset(position, 0);
    } else {
      this.scrollToPosition(position);
    }

  }

  public int getCurrentScrollY() {
    return this.mScrollY;
  }

  public int getHeaderHeight() {
    return this.headerHeight;
  }

  public void setCurrentScrollY(int mScrollY) {
    this.mScrollY = mScrollY;
  }

}

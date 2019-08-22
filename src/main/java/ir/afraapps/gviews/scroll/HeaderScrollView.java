package ir.afraapps.gviews.scroll;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.core.widget.NestedScrollView;


public class HeaderScrollView extends NestedScrollView {
  private int mPrevScrollY;
  private int mScrollY;
  private int offset;
  private int headerHeight;
  private int headerMiniHeight;
  private int toolbarHeight;
  private int toolbarHeightHalf;
  private ScrollViewHeaderCallbacks mCallbacks;
  private HeaderScrollView.ScrollStateHandler stateHandler;

  public HeaderScrollView(Context context) {
    super(context);
    this.init();
  }

  public HeaderScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.init();
  }

  public HeaderScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.init();
  }

  private void init() {
    this.stateHandler = new HeaderScrollView.ScrollStateHandler();
  }

  @Override
  public void onRestoreInstanceState(Parcelable state) {
    if (state instanceof Bundle) {
      Bundle bundle = (Bundle) state;
      this.headerHeight = bundle.getInt("headerHeight");
      this.headerMiniHeight = bundle.getInt("headerMiniHeight");
      this.toolbarHeight = bundle.getInt("toolbarHeight");
      this.toolbarHeightHalf = bundle.getInt("toolbarHeightHalf");
      this.mPrevScrollY = bundle.getInt("prevScrollY");
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
    bundle.putInt("headerHeight", this.headerHeight);
    bundle.putInt("headerMiniHeight", this.headerMiniHeight);
    bundle.putInt("toolbarHeight", this.toolbarHeight);
    bundle.putInt("toolbarHeightHalf", this.toolbarHeightHalf);
    bundle.putInt("prevScrollY", this.mPrevScrollY);
    bundle.putInt("scrollY", this.mScrollY);
    bundle.putInt("offset", this.offset);
    return bundle;
  }

  protected void onScrollEnd() {
    if (this.mScrollY < this.headerHeight || this.offset > 0 && this.offset < this.toolbarHeightHalf) {
      this.offset = 0;
      if (this.mCallbacks != null) {
        this.mCallbacks.onShowToolbar();
      }
    } else if (this.mScrollY > this.headerHeight && this.offset < this.toolbarHeight && this.offset >= this.toolbarHeightHalf) {
      this.offset = this.toolbarHeight;
      if (this.mCallbacks != null) {
        this.mCallbacks.onHideToolbar();
      }
    }

  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    this.mScrollY = t;
    if (this.mCallbacks != null) {
      if (this.mScrollY < this.mPrevScrollY || this.mScrollY > this.headerMiniHeight) {
        this.offset += this.mScrollY - this.mPrevScrollY;
        this.clipOffset();
        if (this.mCallbacks != null) {
          this.mCallbacks.onOffsetChanged(this.offset);
        }
      }

      if (this.mPrevScrollY < this.mScrollY) {
        if (this.mCallbacks != null) {
          this.mCallbacks.onScrollChanged(this.mScrollY, ScrollState.UP);
        }
      } else if (this.mScrollY < this.mPrevScrollY && this.mCallbacks != null) {
        this.mCallbacks.onScrollChanged(this.mScrollY, ScrollState.DOWN);
      }
    }

    this.mPrevScrollY = this.mScrollY;
    this.stateHandler.setTime(System.currentTimeMillis());
  }

  private void clipOffset() {
    if (this.offset < 0) {
      this.offset = 0;
    } else if (this.offset > this.toolbarHeight) {
      this.offset = this.toolbarHeight;
    }

  }

  public void setHeaderItemsHeight(int headerHeight, int toolbarHeight, int headerMiniHeight) {
    this.toolbarHeight = toolbarHeight;
    this.toolbarHeightHalf = toolbarHeight / 2;
    this.headerHeight = headerHeight;
    this.headerMiniHeight = headerMiniHeight;
  }

  public void setScrollViewCallbacks(ScrollViewHeaderCallbacks listener) {
    this.mCallbacks = listener;
  }

  public void scrollVerticallyTo(int y) {
    this.scrollTo(0, y);
  }

  public int getCurrentScrollY() {
    return this.mScrollY;
  }

  public void setCurrentScrollY(int scrollY) {
    this.mScrollY = scrollY;
  }

  @Override
  @SuppressLint({"ClickableViewAccessibility"})
  public boolean onTouchEvent(MotionEvent ev) {
    float height = (float) (this.headerHeight - this.mScrollY);
    switch (ev.getAction()) {
      case 1:
      case 3:
        this.post(this.stateHandler);
    }

    return ev.getY() >= height && super.onTouchEvent(ev);
  }

  @Override
  protected void onDetachedFromWindow() {
    this.removeCallbacks(this.stateHandler);
    super.onDetachedFromWindow();
  }

  private class ScrollStateHandler implements Runnable {
    private long lastScrollUpdate;

    private ScrollStateHandler() {
      this.lastScrollUpdate = -1L;
    }

    public void setTime(long time) {
      this.lastScrollUpdate = time;
    }

    public void resetTime() {
      this.lastScrollUpdate = -1L;
    }

    @Override
    public void run() {
      long currentTime = System.currentTimeMillis();
      if (currentTime - this.lastScrollUpdate > 150L) {
        this.resetTime();
        HeaderScrollView.this.onScrollEnd();
      } else {
        HeaderScrollView.this.postDelayed(this, 150L);
      }

    }
  }
}

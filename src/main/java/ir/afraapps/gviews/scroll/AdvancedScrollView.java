package ir.afraapps.gviews.scroll;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;

import androidx.core.widget.NestedScrollView;


public class AdvancedScrollView extends NestedScrollView {
  private int mPrevScrollY;
  private int mScrollY;
  private ScrollViewCallbacks mCallbacks;

  public AdvancedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public AdvancedScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AdvancedScrollView(Context context) {
    super(context);
  }

  @Override
  public void onRestoreInstanceState(Parcelable state) {
    if (state instanceof Bundle) {
      Bundle bundle = (Bundle) state;
      this.mPrevScrollY = bundle.getInt("prevScrollY");
      this.mScrollY = bundle.getInt("scrollY");
      state = bundle.getParcelable("superState");
    }

    super.onRestoreInstanceState(state);
  }

  @Override
  public Parcelable onSaveInstanceState() {
    Bundle bundle = new Bundle();
    bundle.putParcelable("superState", super.onSaveInstanceState());
    bundle.putInt("prevScrollY", this.mPrevScrollY);
    bundle.putInt("scrollY", this.mScrollY);
    return bundle;
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    this.mScrollY = t;
    if (this.mCallbacks != null) {
      if (this.mPrevScrollY < this.mScrollY) {
        this.mCallbacks.onScrollChanged(this.mScrollY);
      } else if (this.mScrollY < this.mPrevScrollY) {
        this.mCallbacks.onScrollChanged(this.mScrollY);
      }
    }

    this.mPrevScrollY = this.mScrollY;
  }

  public void setScrollViewCallbacks(ScrollViewCallbacks listener) {
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
}

package ir.afraapps.gviews.scroll;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author ali jabbari on 11/9/2016.
 */

public class DividerRecyclerViewItem extends RecyclerView.ItemDecoration {
  public static final int HORIZONTAL = 0;
  public static final int VERTICAL = 1;
  private static final int[] ATTRS = new int[]{16843284};
  private Drawable mDivider;
  private int marginLeft;
  private int marginTop;
  private int marginRight;
  private int marginBottom = 0;
  private int mColor = 0;
  private int width;
  private int height = 0;
  private int mOrientation;
  private final Rect mBounds = new Rect();

  public DividerRecyclerViewItem(Context context, int orientation) {
    TypedArray a = context.obtainStyledAttributes(ATTRS);
    this.mDivider = a.getDrawable(0);
    a.recycle();
    this.setOrientation(orientation);
  }

  public void setOrientation(int orientation) {
    if (orientation != HORIZONTAL && orientation != VERTICAL) {
      throw new IllegalArgumentException("Invalid orientation. It should be either HORIZONTAL or VERTICAL");
    } else {
      this.mOrientation = orientation;
    }
  }

  public void setDrawable(@NonNull Drawable drawable) {
    this.mDivider = drawable;
  }

  public int getHeight() {
    return this.height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public int getWidth() {
    return this.width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  @Override
  public void onDraw(@NonNull Canvas c, RecyclerView parent, @NonNull RecyclerView.State state) {
    if (parent.getLayoutManager() != null) {
      if (this.mOrientation == VERTICAL) {
        this.drawVertical(c, parent);
      } else {
        this.drawHorizontal(c, parent);
      }

    }
  }

  @SuppressLint({"NewApi"})
  private void drawVertical(Canvas canvas, RecyclerView parent) {
    canvas.save();
    int left;
    int right;
    if (parent.getClipToPadding()) {
      left = parent.getPaddingLeft() + this.marginLeft;
      right = parent.getWidth() - parent.getPaddingRight() - this.marginRight;
      canvas.clipRect(left, parent.getPaddingTop(), right, parent.getHeight() - parent.getPaddingBottom());
    } else {
      left = this.marginLeft;
      right = parent.getWidth() - this.marginRight;
    }

    if (this.mColor != 0) {
      this.mDivider.setColorFilter(this.mColor, PorterDuff.Mode.SRC_ATOP);
    }

    int childCount = parent.getChildCount() - 1;

    for (int i = 0; i < childCount; ++i) {
      View child = parent.getChildAt(i);
      parent.getDecoratedBoundsWithMargins(child, this.mBounds);
      int bottom = this.mBounds.bottom + Math.round(ViewCompat.getTranslationY(child)) - this.marginBottom;
      int top = bottom - (this.height == 0 ? this.mDivider.getIntrinsicHeight() : this.height);
      this.mDivider.setBounds(left, top, right, bottom);
      this.mDivider.draw(canvas);
    }

    canvas.restore();
  }

  @SuppressLint({"NewApi"})
  private void drawHorizontal(Canvas canvas, RecyclerView parent) {
    canvas.save();
    int top;
    int bottom;
    if (parent.getClipToPadding()) {
      top = parent.getPaddingTop() + this.marginTop;
      bottom = parent.getHeight() - parent.getPaddingBottom() - this.marginBottom;
      canvas.clipRect(parent.getPaddingLeft(), top, parent.getWidth() - parent.getPaddingRight(), bottom);
    } else {
      top = this.marginTop;
      bottom = parent.getHeight() - this.marginBottom;
    }

    if (this.mColor != 0) {
      this.mDivider.setColorFilter(this.mColor, PorterDuff.Mode.SRC_ATOP);
    }

    int childCount = parent.getChildCount() - 1;

    for (int i = 0; i < childCount; ++i) {
      View child = parent.getChildAt(i);
      parent.getLayoutManager().getDecoratedBoundsWithMargins(child, this.mBounds);
      int right = this.mBounds.right + Math.round(ViewCompat.getTranslationX(child)) - this.marginRight;
      int left = right - (this.width == 0 ? this.mDivider.getIntrinsicWidth() : this.width);
      this.mDivider.setBounds(left, top, right, bottom);
      this.mDivider.draw(canvas);
    }

    canvas.restore();
  }

  @Override
  public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
    if (this.mOrientation == 1) {
      outRect.set(0, 0, 0, this.mDivider.getIntrinsicHeight() + this.marginBottom + this.marginTop);
    } else {
      outRect.set(0, 0, this.mDivider.getIntrinsicWidth() + this.marginLeft + this.marginRight, 0);
    }

  }

  public void setDividerColor(int color) {
    this.mColor = color;
  }

  public void setMargins(int left, int top, int right, int bottom) {
    this.marginLeft = left;
    this.marginTop = top;
    this.marginRight = right;
    this.marginBottom = bottom;
  }

  public void setMargins(int margin) {
    this.marginLeft = margin;
    this.marginTop = margin;
    this.marginRight = margin;
    this.marginBottom = margin;
  }

  public void setMarginLeft(int marginLeft) {
    this.marginLeft = marginLeft;
  }

  public void setMarginTop(int marginTop) {
    this.marginTop = marginTop;
  }

  public void setMarginRight(int marginRight) {
    this.marginRight = marginRight;
  }

  public void setMarginBottom(int marginBottom) {
    this.marginBottom = marginBottom;
  }
}

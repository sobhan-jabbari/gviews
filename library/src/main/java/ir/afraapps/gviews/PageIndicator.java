package ir.afraapps.gviews;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


public class PageIndicator extends AppCompatImageView implements
  ViewPager.OnPageChangeListener, ViewPager.OnAdapterChangeListener {

  private Paint mPaintStroke;
  private Paint mPaintFill;
  private int mCurrentPage;
  private boolean mCentered;
  private float mRadius;
  private float mGapWidth;
  private float mPercent;
  private ViewPager viewPager;


  public PageIndicator(Context context) {
    this(context, null);
  }


  public PageIndicator(Context context, AttributeSet attrs) {
    this(context, attrs, R.attr.PageIndicatorStyle);
  }


  public PageIndicator(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initPaints();

    if (isInEditMode())
      return;

    final Resources res = getResources();

    //Load defaults from resources
    final int defaultSelectedColor = ContextCompat.getColor(context, R.color.default_line_indicator_selected_color);
    final int defaultUnselectedColor = ContextCompat.getColor(context, R.color.default_line_indicator_unselected_color);
    final float defaultRadius = res.getDimension(R.dimen.default_indicator_radius);
    final float defaultGapWidth = res.getDimension(R.dimen.default_indicator_gap_width);
    final float defaultStrokeWidth = res.getDimension(R.dimen.default_indicator_stroke_width);
    final boolean defaultCentered = true;

    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PageIndicator, defStyle, 0);

    mCentered = a.getBoolean(R.styleable.PageIndicator_centered, defaultCentered);
    mRadius = a.getDimension(R.styleable.PageIndicator_lineWidth, defaultRadius);
    mGapWidth = a.getDimension(R.styleable.PageIndicator_gapWidth, defaultGapWidth);
    setStrokeWidth(a.getDimension(R.styleable.PageIndicator_strokeWidth, defaultStrokeWidth));
    mPaintStroke.setColor(a.getColor(R.styleable.PageIndicator_unselectedColor, defaultUnselectedColor));
    mPaintFill.setColor(a.getColor(R.styleable.PageIndicator_selectedColor, defaultSelectedColor));

    Drawable background = a.getDrawable(R.styleable.PageIndicator_android_background);
    if (background != null) {
      setBackground(background);
    }

    a.recycle();

  }


  private void initPaints() {
    mPaintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaintStroke.setStyle(Style.FILL_AND_STROKE);

    mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaintStroke.setStyle(Style.STROKE);
  }

  @Override
  protected void onDetachedFromWindow() {
    if (viewPager != null) {
      viewPager.removeOnPageChangeListener(this);
    }
    super.onDetachedFromWindow();
  }

  /*

  public void setIndicatorCount(int count) {
    mCount = count;
    invalidate();
  }


  public int getIndicatorCount() {
    return mCount;
  }
*/

  public void setViewPager(ViewPager viewPager) {
    if (this.viewPager != null) {
      this.viewPager.removeOnPageChangeListener(this);
    }
    this.viewPager = viewPager;
    if (viewPager != null) {
      viewPager.addOnPageChangeListener(this);
      viewPager.addOnAdapterChangeListener(this);
    }
    requestLayout();
  }


  public void setPercent(float percent) {
    this.mPercent = percent;
    invalidate();
  }


  public void setCentered(boolean centered) {
    mCentered = centered;
    invalidate();
  }


  public boolean isCentered() {
    return mCentered;
  }


  public void setUnselectedColor(int unselectedColor) {
    mPaintStroke.setColor(unselectedColor);
    invalidate();
  }


  public int getUnselectedColor() {
    return mPaintStroke.getColor();
  }


  public void setSelectedColor(int selectedColor) {
    mPaintFill.setColor(selectedColor);
    invalidate();
  }


  public int getSelectedColor() {
    return mPaintFill.getColor();
  }


  public void setRadius(float radius) {
    mRadius = radius;
    invalidate();
  }


  public float getRadius() {
    return mRadius;
  }


  public void setStrokeWidth(float lineHeight) {
    mPaintFill.setStrokeWidth(lineHeight);
    mPaintStroke.setStrokeWidth(lineHeight);
    invalidate();
  }


  public float getStrokeWidth() {
    return mPaintFill.getStrokeWidth();
  }


  public void setGapWidth(float gapWidth) {
    mGapWidth = gapWidth;
    invalidate();
  }


  public float getGapWidth() {
    return mGapWidth;
  }


  private int getPageCount() {
    if (viewPager == null || viewPager.getAdapter() == null) {
      return 0;
    }
    return viewPager.getAdapter().getCount();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    int count = getPageCount();

    if (count <= 1) {
      return;
    }

    if (mCurrentPage >= count) {
      setCurrentItem(0);
      return;
    }

    final float diameterAndGap = (mRadius * 2) + mGapWidth;
    final float indicatorWidth = (count * diameterAndGap) - mGapWidth;
    final float paddingTop = getPaddingTop();
    final float paddingLeft = getPaddingLeft();
    final float paddingRight = getPaddingRight();

    final float dy = paddingTop + ((getHeight() - paddingTop - getPaddingBottom()) / 2.0f);

    float horizontalOffset = paddingLeft;
    if (mCentered) {
      horizontalOffset += ((getWidth() - paddingLeft - paddingRight) / 2.0f) - (indicatorWidth / 2.0f);
    }

    for (int i = 0; i < count; i++) {
      float dx = horizontalOffset + (i * diameterAndGap);
      canvas.drawCircle(dx, dy, mRadius, mPaintStroke);

      boolean canDrawSelected = false;
      if (i == mCurrentPage) {
        mPaintFill.setAlpha((int) ((1.0f - mPercent) * 255));
        canDrawSelected = true;
      }

      if (i == mCurrentPage + 1) {
        mPaintFill.setAlpha((int) (mPercent * 255));
        canDrawSelected = true;
      }

      if (canDrawSelected) {
        canvas.drawCircle(dx, dy, mRadius, mPaintFill);
      }
    }
  }


  public void setCurrentItem(int position) {
    mCurrentPage = position;
    // invalidate();
  }


  public void notifyDataSetChanged() {
    invalidate();
  }


  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int pageCount = getPageCount();
    if (pageCount <= 1) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    } else {
      setMeasuredDimension(measureWidth(pageCount, widthMeasureSpec), measureHeight(heightMeasureSpec));
    }
  }


  private int measureWidth(int pageCount, int measureSpec) {
    float result;
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);

    if ((specMode == MeasureSpec.EXACTLY)) {
      //We were told how big to be
      result = specSize;
    } else {
      //Calculate the width according the views count
      result = getPaddingLeft() + getPaddingRight() + (pageCount * mRadius) + ((pageCount - 1) * mGapWidth);
      //Respect AT_MOST value if that was what is called for by measureSpec
      if (specMode == MeasureSpec.AT_MOST) {
        result = Math.min(result, specSize);
      }
    }
    return (int) Math.ceil(result);
  }


  private int measureHeight(int measureSpec) {
    float result;
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);

    if (specMode == MeasureSpec.EXACTLY) {
      //We were told how big to be
      result = specSize;
    } else {
      //Measure the height
      result = (mRadius * 2) + mPaintFill.getStrokeWidth() + getPaddingTop() + getPaddingBottom();
      //Respect AT_MOST value if that was what is called for by measureSpec
      if (specMode == MeasureSpec.AT_MOST) {
        result = Math.min(result, specSize);
      }
    }
    return (int) Math.ceil(result);
  }


  @Override
  public void onRestoreInstanceState(Parcelable state) {
    SavedState savedState = (SavedState) state;
    super.onRestoreInstanceState(savedState.getSuperState());
    mCurrentPage = savedState.currentPage;
    requestLayout();
  }


  @Override
  public Parcelable onSaveInstanceState() {
    Parcelable superState = super.onSaveInstanceState();
    SavedState savedState = new SavedState(superState);
    savedState.currentPage = mCurrentPage;
    return savedState;
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    if (viewPager == null) {
      return;
    }

    setPercent(positionOffset);
    setCurrentItem(position);
  }

  @Override
  public void onPageSelected(int position) {
  }

  @Override
  public void onPageScrollStateChanged(int state) {
  }

  @Override
  public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
    requestLayout();
    invalidate();
  }


  private static class SavedState extends BaseSavedState {

    int currentPage;


    public SavedState(Parcelable superState) {
      super(superState);
    }


    private SavedState(Parcel in) {
      super(in);
      currentPage = in.readInt();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeInt(currentPage);
    }

    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

      @Override
      public SavedState createFromParcel(Parcel in) {
        return new SavedState(in);
      }


      @Override
      public SavedState[] newArray(int size) {
        return new SavedState[size];
      }
    };
  }

}
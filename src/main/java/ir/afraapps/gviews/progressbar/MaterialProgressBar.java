/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package ir.afraapps.gviews.progressbar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.TintTypedArray;
import ir.afraapps.gviews.R;
import ir.afraapps.gviews.progressbar.compat.DrawableCompat;


/**
 * A {@link ProgressBar} subclass that handles tasks related to back ported progress drawable.
 */
public class MaterialProgressBar extends ProgressBar {
  private static final String TAG = MaterialProgressBar.class.getSimpleName();
  public static final int PROGRESS_STYLE_CIRCULAR = 0;
  public static final int PROGRESS_STYLE_HORIZONTAL = 1;
  private int mProgressStyle;
  private boolean indeterminate;
  private MaterialProgressBar.TintInfo mProgressTintInfo = new MaterialProgressBar.TintInfo();

  public MaterialProgressBar(Context context) {
    super(context);
    this.init(null, 0, 0);
  }

  public MaterialProgressBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.init(attrs, 0, 0);
  }

  public MaterialProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.init(attrs, defStyleAttr, 0);
  }

  @TargetApi(21)
  public MaterialProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    this.init(attrs, defStyleAttr, defStyleRes);
  }

  @SuppressLint({"RestrictedApi"})
  private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    Context context = this.getContext();
    TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.MaterialProgressBar, defStyleAttr, defStyleRes);
    this.mProgressStyle = a.getInt(R.styleable.MaterialProgressBar_mpb_progressStyle, PROGRESS_STYLE_CIRCULAR);
    this.indeterminate = a.getBoolean(R.styleable.MaterialProgressBar_mpb_indeterminate, true);
    this.setIndeterminate(this.indeterminate);
    float progress = a.getFloat(R.styleable.MaterialProgressBar_mpb_progress, -1.0F);
    float thickness = a.getFloat(R.styleable.MaterialProgressBar_mpb_thickness, -1.0F);
    boolean smoothProgress = a.getBoolean(R.styleable.MaterialProgressBar_mpb_smooth_progress, false);
    boolean setBothDrawables = a.getBoolean(R.styleable.MaterialProgressBar_mpb_setBothDrawables, false);
    boolean useIntrinsicPadding = a.getBoolean(R.styleable.MaterialProgressBar_mpb_useIntrinsicPadding, true);
    boolean showProgressBackground = a.getBoolean(R.styleable.MaterialProgressBar_mpb_showProgressBackground, this.mProgressStyle == PROGRESS_STYLE_HORIZONTAL);
    if (a.hasValue(R.styleable.MaterialProgressBar_mpb_progressTint)) {
      this.mProgressTintInfo.mProgressTint = a.getColorStateList(R.styleable.MaterialProgressBar_mpb_progressTint);
      this.mProgressTintInfo.mHasProgressTint = true;
    }

    if (a.hasValue(R.styleable.MaterialProgressBar_mpb_progressTintMode)) {
      this.mProgressTintInfo.mProgressTintMode = DrawableCompat.parseTintMode(a.getInt(R.styleable.MaterialProgressBar_mpb_progressTintMode, -1), null);
      this.mProgressTintInfo.mHasProgressTintMode = true;
    }

    if (a.hasValue(R.styleable.MaterialProgressBar_mpb_secondaryProgressTint)) {
      this.mProgressTintInfo.mSecondaryProgressTint = a.getColorStateList(R.styleable.MaterialProgressBar_mpb_secondaryProgressTint);
      this.mProgressTintInfo.mHasSecondaryProgressTint = true;
    }

    if (a.hasValue(R.styleable.MaterialProgressBar_mpb_secondaryProgressTintMode)) {
      this.mProgressTintInfo.mSecondaryProgressTintMode = DrawableCompat.parseTintMode(a.getInt(R.styleable.MaterialProgressBar_mpb_secondaryProgressTintMode, -1), null);
      this.mProgressTintInfo.mHasSecondaryProgressTintMode = true;
    }

    if (a.hasValue(R.styleable.MaterialProgressBar_mpb_progressBackgroundTint)) {
      this.mProgressTintInfo.mProgressBackgroundTint = a.getColorStateList(R.styleable.MaterialProgressBar_mpb_progressBackgroundTint);
      this.mProgressTintInfo.mHasProgressBackgroundTint = true;
    }

    if (a.hasValue(R.styleable.MaterialProgressBar_mpb_progressBackgroundTintMode)) {
      this.mProgressTintInfo.mProgressBackgroundTintMode = DrawableCompat.parseTintMode(a.getInt(R.styleable.MaterialProgressBar_mpb_progressBackgroundTintMode, -1), null);
      this.mProgressTintInfo.mHasProgressBackgroundTintMode = true;
    }

    if (a.hasValue(R.styleable.MaterialProgressBar_mpb_indeterminateTint)) {
      this.mProgressTintInfo.mIndeterminateTint = a.getColorStateList(R.styleable.MaterialProgressBar_mpb_indeterminateTint);
      this.mProgressTintInfo.mHasIndeterminateTint = true;
    }

    if (a.hasValue(R.styleable.MaterialProgressBar_mpb_indeterminateTintMode)) {
      this.mProgressTintInfo.mIndeterminateTintMode = DrawableCompat.parseTintMode(a.getInt(R.styleable.MaterialProgressBar_mpb_indeterminateTintMode, -1), null);
      this.mProgressTintInfo.mHasIndeterminateTintMode = true;
    }

    a.recycle();
    switch (this.mProgressStyle) {
      case PROGRESS_STYLE_CIRCULAR:
        if (setBothDrawables) {
          throw new UnsupportedOperationException("Determinate circular drawable is not yet supported");
        }

        if (!this.isInEditMode()) {
          this.setIndeterminateDrawable(new IndeterminateProgressDrawable(context, thickness, this.indeterminate, progress, smoothProgress));
        }
        break;
      case PROGRESS_STYLE_HORIZONTAL:
        if ((this.isIndeterminate() || setBothDrawables) && !this.isInEditMode()) {
          this.setIndeterminateDrawable(new IndeterminateHorizontalProgressDrawable(context));
        }

        if (!this.isIndeterminate() || setBothDrawables) {
          this.setProgressDrawable(new HorizontalProgressDrawable(context));
        }
        break;
      default:
        throw new IllegalArgumentException("Unknown progress style: " + this.mProgressStyle);
    }

    this.setUseIntrinsicPadding(useIntrinsicPadding);
    this.setShowProgressBackground(showProgressBackground);
  }

  public void setMaterialIndeterminate(boolean indeterminate) {
    this.indeterminate = indeterminate;
    Drawable drawable = this.getIndeterminateDrawable();
    if (drawable instanceof IndeterminateProgressDrawable) {
      ((IndeterminateProgressDrawable) drawable).setIndeterminate(indeterminate);
    }

  }

  public boolean isMaterialIndeterminate() {
    return this.indeterminate;
  }

  public void setSmoothProgress(boolean smoothProgress) {
    Drawable drawable = this.getIndeterminateDrawable();
    if (drawable instanceof IndeterminateProgressDrawable) {
      ((IndeterminateProgressDrawable) drawable).setSmoothProgress(smoothProgress);
    }

  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    this.fixCanvasScalingWhenHardwareAccelerated();
  }

  private void fixCanvasScalingWhenHardwareAccelerated() {
    if (Build.VERSION.SDK_INT < 18 && this.isHardwareAccelerated() && this.getLayerType() != View.LAYER_TYPE_SOFTWARE) {
      this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

  }

  public int getProgressStyle() {
    return this.mProgressStyle;
  }

  @Override
  public Drawable getCurrentDrawable() {
    return this.isIndeterminate() ? this.getIndeterminateDrawable() : this.getProgressDrawable();
  }

  public boolean getUseIntrinsicPadding() {
    Drawable drawable = this.getCurrentDrawable();
    if (drawable instanceof IntrinsicPaddingDrawable) {
      return ((IntrinsicPaddingDrawable) drawable).getUseIntrinsicPadding();
    } else {
      throw new IllegalStateException("Drawable does not implement IntrinsicPaddingDrawable");
    }
  }

  public void setUseIntrinsicPadding(boolean useIntrinsicPadding) {
    Drawable drawable = this.getCurrentDrawable();
    if (drawable instanceof IntrinsicPaddingDrawable) {
      ((IntrinsicPaddingDrawable) drawable).setUseIntrinsicPadding(useIntrinsicPadding);
    }

    Drawable indeterminateDrawable = this.getIndeterminateDrawable();
    if (indeterminateDrawable instanceof IntrinsicPaddingDrawable) {
      ((IntrinsicPaddingDrawable) indeterminateDrawable).setUseIntrinsicPadding(useIntrinsicPadding);
    }

    Drawable progressDrawable = this.getProgressDrawable();
    if (progressDrawable instanceof HorizontalProgressDrawable) {
      ((HorizontalProgressDrawable) progressDrawable).setUseIntrinsicPadding(useIntrinsicPadding);
    }

  }

  public boolean getShowProgressBackground() {
    Drawable drawable = this.getCurrentDrawable();
    return drawable instanceof ShowBackgroundDrawable && ((ShowBackgroundDrawable) drawable).getShowBackground();
  }

  public void setShowProgressBackground(boolean show) {
    Drawable drawable = this.getCurrentDrawable();
    if (drawable instanceof ShowBackgroundDrawable) {
      ((ShowBackgroundDrawable) drawable).setShowBackground(show);
    }

    Drawable indeterminateDrawable = this.getIndeterminateDrawable();
    if (indeterminateDrawable instanceof ShowBackgroundDrawable) {
      ((ShowBackgroundDrawable) indeterminateDrawable).setShowBackground(show);
    }

  }

  @Override
  public void setProgressDrawable(Drawable d) {
    super.setProgressDrawable(d);
    if (this.mProgressTintInfo != null) {
      this.applyProgressTints();
    }

  }

  @Override
  public void setIndeterminateDrawable(Drawable d) {
    super.setIndeterminateDrawable(d);
    if (this.mProgressTintInfo != null) {
      this.applyIndeterminateTint();
    }

  }

  @Override
  @Nullable
  public ColorStateList getProgressTintList() {
    return this.mProgressTintInfo.mProgressTint;
  }

  @Override
  public void setProgressTintList(@Nullable ColorStateList tint) {
    this.mProgressTintInfo.mProgressTint = tint;
    this.mProgressTintInfo.mHasProgressTint = true;
    this.applyPrimaryProgressTint();
  }

  @Override
  @Nullable
  public PorterDuff.Mode getProgressTintMode() {
    return this.mProgressTintInfo.mProgressTintMode;
  }

  @Override
  public void setProgressTintMode(@Nullable PorterDuff.Mode tintMode) {
    this.mProgressTintInfo.mProgressTintMode = tintMode;
    this.mProgressTintInfo.mHasProgressTintMode = true;
    this.applyPrimaryProgressTint();
  }

  @Override
  @Nullable
  public ColorStateList getSecondaryProgressTintList() {
    return this.mProgressTintInfo.mSecondaryProgressTint;
  }

  @Override
  public void setSecondaryProgressTintList(@Nullable ColorStateList tint) {
    this.mProgressTintInfo.mSecondaryProgressTint = tint;
    this.mProgressTintInfo.mHasSecondaryProgressTint = true;
    this.applySecondaryProgressTint();
  }

  @Override
  @Nullable
  public PorterDuff.Mode getSecondaryProgressTintMode() {
    return this.mProgressTintInfo.mSecondaryProgressTintMode;
  }

  @Override
  public void setSecondaryProgressTintMode(@Nullable PorterDuff.Mode tintMode) {
    this.mProgressTintInfo.mSecondaryProgressTintMode = tintMode;
    this.mProgressTintInfo.mHasSecondaryProgressTintMode = true;
    this.applySecondaryProgressTint();
  }

  @Override
  @Nullable
  public ColorStateList getProgressBackgroundTintList() {
    return this.mProgressTintInfo.mProgressBackgroundTint;
  }

  @Override
  public void setProgressBackgroundTintList(@Nullable ColorStateList tint) {
    this.mProgressTintInfo.mProgressBackgroundTint = tint;
    this.mProgressTintInfo.mHasProgressBackgroundTint = true;
    this.applyProgressBackgroundTint();
  }

  @Override
  @Nullable
  public PorterDuff.Mode getProgressBackgroundTintMode() {
    return this.mProgressTintInfo.mProgressBackgroundTintMode;
  }

  @Override
  public void setProgressBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {
    this.mProgressTintInfo.mProgressBackgroundTintMode = tintMode;
    this.mProgressTintInfo.mHasProgressBackgroundTintMode = true;
    this.applyProgressBackgroundTint();
  }

  @Override
  @Nullable
  public ColorStateList getIndeterminateTintList() {
    return this.mProgressTintInfo.mIndeterminateTint;
  }

  @Override
  public void setIndeterminateTintList(@Nullable ColorStateList tint) {
    this.mProgressTintInfo.mIndeterminateTint = tint;
    this.mProgressTintInfo.mHasIndeterminateTint = true;
    this.applyIndeterminateTint();
  }

  @Override
  @Nullable
  public PorterDuff.Mode getIndeterminateTintMode() {
    return this.mProgressTintInfo.mIndeterminateTintMode;
  }

  @Override
  public void setIndeterminateTintMode(@Nullable PorterDuff.Mode tintMode) {
    this.mProgressTintInfo.mIndeterminateTintMode = tintMode;
    this.mProgressTintInfo.mHasIndeterminateTintMode = true;
    this.applyIndeterminateTint();
  }

  private void applyProgressTints() {
    if (this.getProgressDrawable() != null) {
      this.applyPrimaryProgressTint();
      this.applyProgressBackgroundTint();
      this.applySecondaryProgressTint();
    }
  }

  private void applyPrimaryProgressTint() {
    if (this.getProgressDrawable() != null) {
      if (this.mProgressTintInfo.mHasProgressTint || this.mProgressTintInfo.mHasProgressTintMode) {
        Drawable target = this.getTintTargetFromProgressDrawable(16908301, true);
        if (target != null) {
          this.applyTintForDrawable(target, this.mProgressTintInfo.mProgressTint, this.mProgressTintInfo.mHasProgressTint, this.mProgressTintInfo.mProgressTintMode, this.mProgressTintInfo.mHasProgressTintMode);
        }
      }

    }
  }

  private void applySecondaryProgressTint() {
    if (this.getProgressDrawable() != null) {
      if (this.mProgressTintInfo.mHasSecondaryProgressTint || this.mProgressTintInfo.mHasSecondaryProgressTintMode) {
        Drawable target = this.getTintTargetFromProgressDrawable(16908303, false);
        if (target != null) {
          this.applyTintForDrawable(target, this.mProgressTintInfo.mSecondaryProgressTint, this.mProgressTintInfo.mHasSecondaryProgressTint, this.mProgressTintInfo.mSecondaryProgressTintMode, this.mProgressTintInfo.mHasSecondaryProgressTintMode);
        }
      }

    }
  }

  private void applyProgressBackgroundTint() {
    if (this.getProgressDrawable() != null) {
      if (this.mProgressTintInfo.mHasProgressBackgroundTint || this.mProgressTintInfo.mHasProgressBackgroundTintMode) {
        Drawable target = this.getTintTargetFromProgressDrawable(16908288, false);
        if (target != null) {
          this.applyTintForDrawable(target, this.mProgressTintInfo.mProgressBackgroundTint, this.mProgressTintInfo.mHasProgressBackgroundTint, this.mProgressTintInfo.mProgressBackgroundTintMode, this.mProgressTintInfo.mHasProgressBackgroundTintMode);
        }
      }

    }
  }

  private Drawable getTintTargetFromProgressDrawable(int layerId, boolean shouldFallback) {
    Drawable progressDrawable = this.getProgressDrawable();
    if (progressDrawable == null) {
      return null;
    } else {
      progressDrawable.mutate();
      Drawable layerDrawable = null;
      if (progressDrawable instanceof LayerDrawable) {
        layerDrawable = ((LayerDrawable) progressDrawable).findDrawableByLayerId(layerId);
      }

      if (layerDrawable == null && shouldFallback) {
        layerDrawable = progressDrawable;
      }

      return layerDrawable;
    }
  }

  private void applyIndeterminateTint() {
    Drawable indeterminateDrawable = this.getIndeterminateDrawable();
    if (indeterminateDrawable != null) {
      if (this.mProgressTintInfo.mHasIndeterminateTint || this.mProgressTintInfo.mHasIndeterminateTintMode) {
        indeterminateDrawable.mutate();
        this.applyTintForDrawable(indeterminateDrawable, this.mProgressTintInfo.mIndeterminateTint, this.mProgressTintInfo.mHasIndeterminateTint, this.mProgressTintInfo.mIndeterminateTintMode, this.mProgressTintInfo.mHasIndeterminateTintMode);
      }

    }
  }

  public void setProgress(float progress) {
    Drawable drawable = this.getIndeterminateDrawable();
    if (drawable instanceof IndeterminateProgressDrawable) {
      ((IndeterminateProgressDrawable) drawable).setProgress(progress);
    }

  }

  @SuppressLint({"NewApi"})
  private void applyTintForDrawable(Drawable drawable, ColorStateList tint, boolean hasTint, PorterDuff.Mode tintMode, boolean hasTintMode) {
    if (hasTint || hasTintMode) {
      if (hasTint) {
        if (drawable instanceof TintableDrawable) {
          ((TintableDrawable) drawable).setTintList(tint);
        } else {
          Log.w(TAG, "Drawable did not implement TintableDrawable, it won't be tinted below Lollipop");
          if (Build.VERSION.SDK_INT >= 21) {
            drawable.setTintList(tint);
          }
        }
      }

      if (hasTintMode) {
        if (drawable instanceof TintableDrawable) {
          ((TintableDrawable) drawable).setTintMode(tintMode);
        } else {
          Log.w(TAG, "Drawable did not implement TintableDrawable, it won't be tinted below Lollipop");
          if (Build.VERSION.SDK_INT >= 21) {
            drawable.setTintMode(tintMode);
          }
        }
      }

      if (drawable.isStateful()) {
        drawable.setState(this.getDrawableState());
      }
    }

  }

  private static class TintInfo {
    public ColorStateList mProgressTint;
    public PorterDuff.Mode mProgressTintMode;
    public boolean mHasProgressTint;
    public boolean mHasProgressTintMode;
    public ColorStateList mSecondaryProgressTint;
    public PorterDuff.Mode mSecondaryProgressTintMode;
    public boolean mHasSecondaryProgressTint;
    public boolean mHasSecondaryProgressTintMode;
    public ColorStateList mProgressBackgroundTint;
    public PorterDuff.Mode mProgressBackgroundTintMode;
    public boolean mHasProgressBackgroundTint;
    public boolean mHasProgressBackgroundTintMode;
    public ColorStateList mIndeterminateTint;
    public PorterDuff.Mode mIndeterminateTintMode;
    public boolean mHasIndeterminateTint;
    public boolean mHasIndeterminateTintMode;

    private TintInfo() {
    }
  }
}

package ir.afraapps.gviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Random;

/**
 * In the name of Allah
 * <p>
 * Created by ali on 14/05/17.
 */
public class Visualizer extends LinearLayout {

  private int FORMAT = 0;
  private int VISUALIZER_WIDTH = 100;
  private int VISUALIZER_HEIGHT = 200;
  private int VISUALIZER_NUM_WAVES = 15;
  private int VISUALIZER_GRAVITY = 0;

  private int LINE_WIDTH = 20;
  private int LINE_MIN_WIDTH = 20;
  private int LINE_HEIGHT = 20;
  private int LINE_MIN_HEIGHT = 20;
  private int LINE_SPACING = 10;
  private int LINE_BORDER_RADIUS = 50;

  private int COLOR_UNIFORM = Color.BLACK;
  private boolean COLOR_IS_GRADIENT = false;
  private int COLOR_GRADIENT_START = Color.WHITE;
  private int COLOR_GRADIENT_END = Color.BLACK;

  private Context context;
  private Random randomNum = new Random();
  private LayoutParams params;
  private ArrayList<View> waveList = new ArrayList<>();

  public Visualizer(Context context) {
    super(context);
    this.context = context;
    if (!isInEditMode()) {
      this.init();
    }
  }

  public Visualizer(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;

    if (!isInEditMode()) {
      this.attributes(attrs);
      this.init();
    }
  }

  public Visualizer(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.context = context;

    if (!isInEditMode()) {
      this.attributes(attrs);
      this.init();
    }
  }

  private void attributes(AttributeSet attrs) {
    TypedArray a = context.getTheme().obtainStyledAttributes(
      attrs,
      R.styleable.Visualizer,
      0, 0);

    try {
      FORMAT = a.getInteger(R.styleable.Visualizer_aw_format, FORMAT);
      VISUALIZER_HEIGHT = a.getDimensionPixelSize(R.styleable.Visualizer_aw_height, VISUALIZER_HEIGHT);
      VISUALIZER_WIDTH = a.getDimensionPixelSize(R.styleable.Visualizer_aw_width, VISUALIZER_WIDTH);
      VISUALIZER_NUM_WAVES = a.getInteger(R.styleable.Visualizer_aw_num_waves, VISUALIZER_NUM_WAVES);
      VISUALIZER_GRAVITY = a.getInteger(R.styleable.Visualizer_aw_gravity, VISUALIZER_GRAVITY);

      LINE_WIDTH = a.getDimensionPixelSize(R.styleable.Visualizer_aw_line_with, LINE_WIDTH);
      LINE_MIN_WIDTH = a.getDimensionPixelSize(R.styleable.Visualizer_aw_line_min_with, LINE_MIN_WIDTH);
      LINE_HEIGHT = a.getDimensionPixelSize(R.styleable.Visualizer_aw_line_height, LINE_HEIGHT);
      LINE_MIN_HEIGHT = a.getDimensionPixelSize(R.styleable.Visualizer_aw_line_min_height, LINE_MIN_HEIGHT);
      LINE_SPACING = a.getDimensionPixelSize(R.styleable.Visualizer_aw_line_spacing, LINE_SPACING);
      LINE_BORDER_RADIUS = a.getInteger(R.styleable.Visualizer_aw_line_border_radius, LINE_BORDER_RADIUS);

      COLOR_UNIFORM = a.getColor(R.styleable.Visualizer_aw_color_uniform, COLOR_UNIFORM);
      COLOR_IS_GRADIENT = a.getBoolean(R.styleable.Visualizer_aw_color_is_gradient, COLOR_IS_GRADIENT);
      COLOR_GRADIENT_START = a.getColor(R.styleable.Visualizer_aw_color_gradient_start, COLOR_GRADIENT_START);
      COLOR_GRADIENT_END = a.getColor(R.styleable.Visualizer_aw_color_gradient_end, COLOR_GRADIENT_END);
    } finally {
      a.recycle();
    }
  }

  private void init() {
    this.setLayoutParams(
      new LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
      )
    );

    switch (FORMAT) {
      case 0:
      case 1:
        this.setOrientation(VERTICAL);
        break;
      case 2:
      case 3:
        this.setOrientation(HORIZONTAL);
        break;
    }

    switch (VISUALIZER_GRAVITY) {
      case 0:
        this.setGravity(Gravity.CENTER);
        break;
      case 1:
        this.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        break;
      case 2:
        this.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        break;
      case 3:
        this.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        break;
      case 4:
        this.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        break;
    }

    this.addWaves();
    this.setRms(1);
  }


  private void addWaves() {
    params = new LayoutParams(LINE_MIN_WIDTH, LINE_MIN_HEIGHT);
    params.setMargins(LINE_SPACING, 0, LINE_SPACING, 0);

    for (int i = 0; i < VISUALIZER_NUM_WAVES; i++) {
      View v = new View(context);
      v.setLayoutParams(params);
      this.setBackground(v);
      waveList.add(v);
      this.addView(v);
    }
  }


  private void setBackground(View v) {
    GradientDrawable gd = new GradientDrawable();
    gd.setCornerRadius(LINE_BORDER_RADIUS);
    gd.setGradientRadius(90.f);

    if (COLOR_IS_GRADIENT) {
      gd.setColors(new int[]{COLOR_GRADIENT_START, COLOR_GRADIENT_END});

      switch (FORMAT) {
        case 0:
          gd.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
          break;
        case 2:
          gd.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
          gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
          break;
        case 1:
          gd.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
          break;
        case 3:
          gd.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
          gd.setGradientType(GradientDrawable.RADIAL_GRADIENT);
          break;
      }
    } else {
      gd.setColors(new int[]{COLOR_UNIFORM, COLOR_UNIFORM});
    }
    v.setBackground(gd);
  }


  public void setVisualizerNumWaves(int count) {
    this.VISUALIZER_NUM_WAVES = count;
    removeAllViews();
    addWaves();
  }


  public void setRms(float rms) {
    int pitch = rms > 0 ? (int) rms + (LINE_MIN_HEIGHT * 4) : 1;
    for (int i = 0; i < waveList.size(); i++) {
      int random = randomNum.nextInt(10 - 1) + 1;
      int size = pitch / random;

      switch (FORMAT) {
        case 0:
          params = new LayoutParams(
            size < LINE_MIN_WIDTH ? LINE_MIN_WIDTH : size,
            LINE_HEIGHT);
          params.setMargins(0, LINE_SPACING, 0, LINE_SPACING);
          break;
        case 1:
          params = new LayoutParams(
            size < LINE_MIN_WIDTH ? LINE_MIN_WIDTH : size / 2,
            size < LINE_MIN_HEIGHT ? LINE_MIN_HEIGHT : size / 2);
          params.setMargins(0, LINE_SPACING, 0, LINE_SPACING);
          break;
        case 2:
          params = new LayoutParams(
            LINE_WIDTH,
            size < LINE_MIN_HEIGHT ? LINE_MIN_HEIGHT : size);
          params.setMargins(LINE_SPACING, 0, LINE_SPACING, 0);
          break;
        case 3:
          params = new LayoutParams(
            size < LINE_MIN_WIDTH ? LINE_MIN_WIDTH : size / 2,
            size < LINE_MIN_HEIGHT ? LINE_MIN_HEIGHT : size / 2);
          params.setMargins(LINE_SPACING, 0, LINE_SPACING, 0);
          break;
      }

      waveList.get(i).setLayoutParams(params);
    }
  }

}

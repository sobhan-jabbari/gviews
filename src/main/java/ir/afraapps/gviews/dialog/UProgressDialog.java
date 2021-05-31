package ir.afraapps.gviews.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import ir.afraapps.gviews.R;


/**
 * In the name of Allah
 * <p>
 * Created by ali on 11/3/17.
 */

public class UProgressDialog {
  private AlertDialog dialog;
  private Context context;
  private String title;
  private int titleColor;
  private boolean darkTheme;
  private boolean cancelable;
  private boolean showButton;
  private Runnable actionDismiss;

  public UProgressDialog(Context context) {
    this.context = context;
  }

  public void show() {
    if (this.dialog != null && this.dialog.isShowing()) {
      this.dialog.dismiss();
    }

    if (this.darkTheme) {
      this.context = this.getDarkThemeContext();
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
    builder.setPositiveButton(null, null);
    builder.setNegativeButton(null, null);
    View content = LayoutInflater.from(this.context).inflate(R.layout.dialog_progress, null);
    TextView txtTitle = content.findViewById(R.id.title);
    if (this.titleColor != 0) {
      txtTitle.setTextColor(this.titleColor);
    }

    if (this.showButton) {
      TextView btn = content.findViewById(R.id.done);
      content.findViewById(R.id.progress).setVisibility(View.GONE);
      btn.setOnClickListener((v) -> {
        this.dialog.dismiss();
      });
      btn.setVisibility(View.VISIBLE);
    } else {
      content.findViewById(R.id.progress).setVisibility(View.VISIBLE);
      content.findViewById(R.id.done).setVisibility(View.GONE);
    }

    txtTitle.setText(this.title);
    builder.setView(content);
    this.dialog = builder.create();
    this.dialog.setCancelable(this.cancelable);
    if (this.actionDismiss != null) {
      this.dialog.setOnDismissListener((dialog) -> {
        this.actionDismiss.run();
      });
    }

    this.dialog.show();
  }

  @SuppressLint({"RestrictedApi"})
  private Context getDarkThemeContext() {
    return new ContextThemeWrapper(this.context, R.style.AlertDialogDarkTheme);
  }

  public UProgressDialog setTitle(String title) {
    this.title = title;
    return this;
  }

  public UProgressDialog setTitle(@StringRes int titleRes) {
    this.title = this.context.getString(titleRes);
    return this;
  }

  public UProgressDialog setTitleColor(@ColorInt int titleColor) {
    this.titleColor = titleColor;
    return this;
  }

  public UProgressDialog setCancelable(boolean cancelable) {
    this.cancelable = cancelable;
    return this;
  }

  public UProgressDialog showButton(boolean showButton) {
    this.showButton = showButton;
    return this;
  }

  public UProgressDialog setDismissAction(Runnable actionDismiss) {
    this.actionDismiss = actionDismiss;
    return this;
  }

  public UProgressDialog useDarkTheme() {
    this.darkTheme = true;
    return this;
  }

  public void dismiss() {
    if (this.dialog != null) {
      this.dialog.dismiss();
    }

  }
}

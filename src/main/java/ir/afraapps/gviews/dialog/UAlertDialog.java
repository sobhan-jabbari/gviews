package ir.afraapps.gviews.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
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

public class UAlertDialog implements DialogInterface.OnClickListener {
  private AlertDialog dialog;
  private Context context;
  private String title;
  private String message;
  private String messageHtml;
  private String positiveLabel;
  private String negativeLabel;
  private int titleColor;
  private int messageColor;
  private int positiveColor;
  private int negativeColor;
  private boolean darkTheme;
  private boolean cancelable;
  private Runnable action;
  private Runnable actionDismiss;
  private Runnable actionCancel;

  public UAlertDialog(Context context) {
    this.context = context;
  }

  public void show() {
    if (this.dialog != null && this.dialog.isShowing()) {
      this.dialog.dismiss();
    }

    if (TextUtils.isEmpty(this.positiveLabel)) {
      this.positiveLabel = this.context.getString(R.string.ok);
    }

    if (this.darkTheme) {
      this.context = this.getDarkThemeContext();
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
    builder.setPositiveButton(this.positiveLabel, this);
    builder.setNegativeButton(this.negativeLabel, this);
    View content = LayoutInflater.from(this.context).inflate(R.layout.dialog_alert_layout, null);
    TextView txtTitle = content.findViewById(R.id.title);
    TextView txtMessage = content.findViewById(R.id.message);
    if (this.titleColor != 0) {
      txtTitle.setTextColor(this.titleColor);
    }

    if (this.messageColor != 0) {
      txtMessage.setTextColor(this.messageColor);
    }

    txtTitle.setText(this.title);
    if (this.messageHtml != null) {
      txtMessage.setText(this.fromHtml(this.messageHtml));
    } else {
      txtMessage.setText(this.message);
    }

    builder.setView(content);
    this.dialog = builder.create();
    this.dialog.setCancelable(this.cancelable);
    if (this.actionDismiss != null) {
      this.dialog.setOnDismissListener((dialog) -> {
        this.actionDismiss.run();
      });
    }

    this.dialog.show();
    if (this.positiveColor != 0) {
      this.dialog.getButton(-1).setTextColor(this.positiveColor);
    }

    if (this.negativeColor != 0) {
      this.dialog.getButton(-2).setTextColor(this.negativeColor);
    }

  }

  private Spanned fromHtml(String text) {
    if (TextUtils.isEmpty(text)) {
      return null;
    } else {
      return Build.VERSION.SDK_INT >= 24 ? Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY) : Html.fromHtml(text);
    }
  }

  @SuppressLint({"RestrictedApi"})
  private Context getDarkThemeContext() {
    return new ContextThemeWrapper(this.context, R.style.AlertDialogDarkTheme);
  }

  public UAlertDialog setTitle(String title) {
    this.title = title;
    return this;
  }

  public UAlertDialog setTitle(@StringRes int titleRes) {
    this.title = this.context.getString(titleRes);
    return this;
  }

  public UAlertDialog setMessage(String message) {
    this.message = message;
    return this;
  }

  public UAlertDialog setMessage(@StringRes int messageRes) {
    this.message = this.context.getString(messageRes);
    return this;
  }

  public UAlertDialog setMessageHtml(String message) {
    this.messageHtml = message;
    return this;
  }

  public UAlertDialog setMessageHtml(@StringRes int messageRes) {
    this.messageHtml = this.context.getString(messageRes);
    return this;
  }

  public UAlertDialog setPositiveLabel(String positiveLabel) {
    this.positiveLabel = positiveLabel;
    return this;
  }

  public UAlertDialog setPositiveLabel(@StringRes int positiveLabelRes) {
    this.positiveLabel = this.context.getString(positiveLabelRes);
    return this;
  }

  public UAlertDialog setNegativeLabel(String negativeLabel) {
    this.negativeLabel = negativeLabel;
    return this;
  }

  public UAlertDialog setNegativeLabel(@StringRes int negativeLabelRes) {
    this.negativeLabel = this.context.getString(negativeLabelRes);
    return this;
  }

  public UAlertDialog setTitleColor(@ColorInt int titleColor) {
    this.titleColor = titleColor;
    return this;
  }

  public UAlertDialog setMessageColor(@ColorInt int messageColor) {
    this.messageColor = messageColor;
    return this;
  }

  public UAlertDialog setPositiveColor(@ColorInt int positiveColor) {
    this.positiveColor = positiveColor;
    return this;
  }

  public UAlertDialog setNegativeColor(@ColorInt int negativeColor) {
    this.negativeColor = negativeColor;
    return this;
  }

  public UAlertDialog setCancelable(boolean cancelable) {
    this.cancelable = cancelable;
    return this;
  }

  public UAlertDialog setAction(Runnable action) {
    this.action = action;
    return this;
  }

  public UAlertDialog setDismissAction(Runnable actionDismiss) {
    this.actionDismiss = actionDismiss;
    return this;
  }

  public UAlertDialog setCancelAction(Runnable actionCancel) {
    this.actionCancel = actionCancel;
    return this;
  }

  public UAlertDialog useDarkTheme() {
    this.darkTheme = true;
    return this;
  }

  @Override
  public void onClick(DialogInterface dialog, int which) {
    switch (which) {
      case -2:
        dialog.dismiss();
        if (this.actionCancel != null) {
          this.actionCancel.run();
        }
        break;
      case -1:
        dialog.dismiss();
        if (this.action != null) {
          this.action.run();
        }
    }

  }
}

package ir.afraapps.gviews.scroll;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author by ali jabbari on 11/9/2016.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
  private static final int HORIZONTAL = 0;
  private static final int VERTICAL = 1;
  private final int mSpaceSize;
  private boolean useSpaceOnLastItem = true;
  private int mOrientation;

  public SpaceItemDecoration(int spaceHeight, int orientation) {
    this.mSpaceSize = spaceHeight;
    this.mOrientation = orientation;
  }

  public void setUseSpaceOnLastItem(boolean useSpaceOnLastItem) {
    this.useSpaceOnLastItem = useSpaceOnLastItem;
  }

  public void setOrientation(int orientation) {
    if (orientation != HORIZONTAL && orientation != VERTICAL) {
      throw new IllegalArgumentException("Invalid orientation. It should be either HORIZONTAL or VERTICAL");
    } else {
      this.mOrientation = orientation;
    }
  }

  @Override
  public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
    if (this.useSpaceOnLastItem) {
      if (this.mOrientation == VERTICAL) {
        outRect.set(0, 0, 0, this.mSpaceSize);
      } else {
        outRect.set(0, 0, this.mSpaceSize, 0);
      }
    } else {
      int itemCount = parent.getAdapter().getItemCount();
      if (parent.getChildAdapterPosition(view) != itemCount - 1) {
        if (this.mOrientation == VERTICAL) {
          outRect.set(0, 0, 0, this.mSpaceSize);
        } else {
          outRect.set(0, 0, this.mSpaceSize, 0);
        }
      }
    }

  }
}

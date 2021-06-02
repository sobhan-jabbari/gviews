package ir.afraapps.gviews.scroll;

public interface HeaderScrollCallbacks {
  void onScrollChanged(int var1);

  void onOffsetChanged(int var1);

  void onHide();

  void onShow();

  void onLoadMore();

  void onFirst();

  void showFabGoUp();

  void hideFabGoUp();
}

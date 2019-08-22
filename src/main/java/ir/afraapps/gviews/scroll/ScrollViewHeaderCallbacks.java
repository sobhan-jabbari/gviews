package ir.afraapps.gviews.scroll;


public interface ScrollViewHeaderCallbacks {

  void onScrollChanged(int scrollY, ScrollState state);

  void onOffsetChanged(int offset);

  void onShowToolbar();

  void onHideToolbar();

}

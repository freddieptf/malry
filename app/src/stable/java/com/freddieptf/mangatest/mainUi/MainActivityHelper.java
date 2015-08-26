package com.freddieptf.mangatest.mainUi;

import android.support.v7.widget.Toolbar;

/**
 * Created by fred on 4/29/15.
 */
public interface MainActivityHelper {
    void lockDrawer(boolean lock);
    boolean isDrawerLocked();
    Toolbar getToolBar();
    void hideToolBarShadow(boolean hide);
    boolean isToolBarShadowVisible();
    MainActivityHelper getMainActivityHelper();
}

package com.freddieptf.mangatest.mainUi;

import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;

import com.freddieptf.mangatest.mainUi.widgets.Cab;

/**
 * Created by fred on 4/29/15.
 */
public interface MainActivityHelper {
    void lockDrawer(boolean lock);
    boolean isDrawerLocked();
    Toolbar getToolBar();
    TabLayout getTabs();
    void hideToolBarShadow(boolean hide);
    boolean isToolBarShadowVisible();
    MainActivityHelper getMainActivityHelper();
    Cab getCab();
}

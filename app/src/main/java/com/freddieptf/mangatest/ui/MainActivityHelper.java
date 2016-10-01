package com.freddieptf.mangatest.ui;

import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;

/**
 * Created by fred on 4/29/15.
 */
public interface MainActivityHelper {
    void hideBottomBar(boolean hide);
    Toolbar getToolBar();
    TabLayout getTabs();
    MainActivityHelper getMainActivityHelper();
}

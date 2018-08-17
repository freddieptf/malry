package com.freddieptf.mangatest.ui.base;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by freddieptf on 27/09/16.
 */

public abstract class BaseActivity extends AppCompatActivity {

    public void setNavigationIcon(int resId) {
    }

    public void setToolbarTitle(String title) {
    }

    public void setToolbarSubTitle(String subTitle) {
    }

    public void hideTabs() {
    }

    public void showTabs() {
    }

    public TabLayout getTabs() {
        return null;
    }
}

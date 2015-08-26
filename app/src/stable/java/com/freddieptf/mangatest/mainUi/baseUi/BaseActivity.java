package com.freddieptf.mangatest.mainUi.baseUi;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.freddieptf.mangatest.utils.ThemeUtilities;

/**
 * Created by fred on 3/25/15.
 */
public class BaseActivity extends AppCompatActivity {


    protected boolean hasNavDrawer() {
        return false;
    }

    protected boolean themeChanged() {
        return themeUtils.themeChanged(theme);
    }

    protected void themeChanged(boolean change){
        if(change){
            setTheme(themeUtils.getCurrentTheme());
            recreate();
        }
    }

    int theme;
    ThemeUtilities themeUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        themeUtils = new ThemeUtilities(this);
        setTheme(themeUtils.getCurrentTheme());
        super.onCreate(savedInstanceState);

        theme = themeUtils.getCurrentTheme();

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//
//            if(hasNavDrawer()){
//                getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
//            }else{
//                getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));
//            }
//
//            getWindow().setNavigationBarColor(getResources().getColor(R.color.primary));
//
//        }



    }

    @Override
    protected void onResume() {
        super.onResume();

        if(themeChanged()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    themeChanged(true);
                }
            }, 0);

        }
    }

}

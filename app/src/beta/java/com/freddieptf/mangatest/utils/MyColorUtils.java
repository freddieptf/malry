package com.freddieptf.mangatest.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import com.freddieptf.mangatest.R;

/**
 * Created by fred on 7/12/15.
 */
public class MyColorUtils {

    Context context;
    final String LOG_TAG = getClass().getSimpleName();

    public MyColorUtils(Context context){
        this.context = context;
    }

    public int getPrimaryTextColor(){
        if(new ThemeUtilities(context).isLight()){
            return R.color.primary_text;
        }else{
            return android.R.color.white;
        }
    }

    public int getAccentColor(){
        return R.color.accent;
    }

    public int getPrimaryColor(){
        return context.getResources().getColor(R.color.primary);
    }

    public int getPrimaryDarkColor(){
        return context.getResources().getColor(R.color.primary_dark);
    }

    @TargetApi(21)
    public void setStatusBarColor(int statusBarColor){
        ((Activity) context).getWindow().setStatusBarColor(statusBarColor);
    }

    public int darken(int color, int defaultDarkColor){
        if(color == getPrimaryColor()) return defaultDarkColor;
        float hsv[] =  new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2]*10/13;
        return Color.HSVToColor(hsv);
    }

}

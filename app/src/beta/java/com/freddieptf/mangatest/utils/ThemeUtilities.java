package com.freddieptf.mangatest.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.freddieptf.mangatest.R;

/**
 * Created by fred on 3/25/15.
 */
public class ThemeUtilities {

    Context context;
    public ThemeUtilities(Context context){
        this.context = context;
    }


    public boolean isLight(){
        boolean isLight = true;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(!sharedPreferences.getString(context.getString(R.string.pref_themes_key),
                context.getString(R.string.pref_theme_light)).equals(context.getString(R.string.pref_theme_light))){
            isLight = false;
        }
        return isLight;
    }


    public int getCurrentTheme(){
        if(isLight()){
            return R.style.MyMangaTheme;
        }else{
            return R.style.MyMangaTheme_ThemeDark;
        }
    }


    public boolean themeChanged(int theme){
        return theme != getCurrentTheme();
    }

    public int getPopUpTheme(){
        if(isLight()){
            return R.style.ThemeOverlay_AppCompat_Light;
        }else{
            return R.style.ThemeOverlay_AppCompat_Dark;
        }
    }

}

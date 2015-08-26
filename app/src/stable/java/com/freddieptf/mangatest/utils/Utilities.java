package com.freddieptf.mangatest.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.Contract;
import com.freddieptf.mangatest.mainUi.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by fred on 2/8/15.
 */
public class Utilities {

    public static void Log(String LOG_TAG, String message){
        if(MainActivity.DEBUG){
            android.util.Log.d(LOG_TAG, "" + message);
        }
    }

    public static boolean isFirstStart(Context context){
        boolean firstStart;
        SharedPreferences prefs = context.getSharedPreferences("firstStart", 0);

        if(!prefs.contains("firstStart")){
            setFirstStart(context, true);
            firstStart = prefs.getBoolean("firstStart", true);
        }else{
            firstStart = prefs.getBoolean("firstStart", true);
        }
        return firstStart;
    }

    public static void setFirstStart(Context context, Boolean b){
        SharedPreferences prefs = context.getSharedPreferences("firstStart", 0);
        SharedPreferences.Editor editor = prefs.edit();
        if(b){
            editor.putBoolean("firstStart", true).apply();
        }else{
            editor.putBoolean("firstStart", false).apply();
        }
    }

    public static void writeMangaPageToPrefs(Context context, String pref, int page){
        SharedPreferences prefs = context.getSharedPreferences(pref, 0);
        SharedPreferences.Editor editor = prefs.edit();
        if(page == 0) prefs.edit().clear().apply();
        else editor.putInt(pref, page).apply();
    }

    public static int readMangaPageFromPrefs(Context context, String pref){
        int page;
        SharedPreferences prefs = context.getSharedPreferences(pref, 0);
        page = prefs.getInt(pref, 0);
        return page;
    }

    public static boolean compactCards(Context context){
        boolean doCompact = true;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if(!sharedPreferences.getBoolean(context.getString(R.string.pref_my_manga_cards_key), false)){
            doCompact = false;
        }

        return doCompact;
    }

    public static boolean wifiOnly(Context context){
        boolean wifiOnly = true;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if(!sharedPreferences.getBoolean(context.getString(R.string.wifi_only_key), false)){
            wifiOnly = false;
        }

        return wifiOnly;
    }

    public static boolean checkWifiState(Context context){
        boolean wifi = false;
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm.getActiveNetworkInfo() != null){
            if(cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_MOBILE){
                wifi = true;
            }
        }

        return wifi;
    }

    public static String getCurrentSource(Context context){
        String source;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(sharedPreferences.getString(
                context.getString(R.string.pref_manga_sources_key), context.getString(R.string.pref_manga_reader))
                .equals(context.getString(R.string.pref_manga_reader))) {
            source = context.getString(R.string.pref_manga_reader);

        }else {
            source = context.getString(R.string.pref_manga_fox);
        }

        return source;

    }

    public static void setCurrentSource(Context context, String source){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(context.getString(R.string.pref_manga_sources_key), source).apply();
    }

    public static Uri getPrefContentUri(Context context){
        Uri uri;

        if(getCurrentSource(context).equals(context.getString(R.string.pref_manga_reader))){
            uri = Contract.MangaReaderMangaList.CONTENT_URI;
        }else {
            uri = Contract.MangaFoxMangaList.CONTENT_URI;
        }

        return uri;
    }



    public static String getMangaStatus(String status){
        String complete = "2";
        String onGoing = "1";

        if(status.trim().equals(complete)){
            return "Complete";
        }else if(status.trim().equals(onGoing)){
            return "On Going";
        }else {
            return "";
        }
    }


    public static Bitmap DownloadBitmapFromUrl(String url){
        Bitmap bitmap = null;
        InputStream inputStream;

        try{
            inputStream = new URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }


    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static boolean externalStorageMounted() {
        String state = Environment.getExternalStorageState();

        if ((Environment.MEDIA_MOUNTED).equals(state)) {
            return true;
        }

        return false;
    }


    public static void hideSystemUi(View view) {
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    public static void showSystemUi(View view){
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public static void changeSystemUiOnTap(final View view, final Context context){

        final GestureDetector clickDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener(){
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        boolean visible =
                                (view.getSystemUiVisibility()
                                        & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;

                        if(visible){
                            hideSystemUi(view);
                        }else {
                            showSystemUi(view);
                            android.os.Handler handler = new android.os.Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    hideSystemUi(view);
                                }
                            }, 2500);
                        }
                        return super.onSingleTapUp(e);
                    }
                });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return clickDetector.onTouchEvent(motionEvent);
            }
        });
    }

    public static void animateHeight(final View view, int from, int to, int duration){
        ValueAnimator valueAnimator = ValueAnimator.ofInt(from, to);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer)valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = value;
                view.setLayoutParams(layoutParams);
            }
        });

        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public static String formatDate(String string){
        final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss.sss";
        String formatedDate;
        Date date = new Date();
        string = string.replace("T", " ");
        string = string.replace("Z", "");
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        try {
            date = sdf.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formatedDate = DateFormat.getDateInstance().format(date);

        return formatedDate.equals("") || formatedDate.isEmpty() ? "Not available" : formatedDate;
    }



}

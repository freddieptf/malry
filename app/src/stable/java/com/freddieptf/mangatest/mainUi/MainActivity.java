package com.freddieptf.mangatest.mainUi;

import android.graphics.Color;
import android.net.http.HttpResponseCache;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.mainUi.baseUi.BaseActivity;
import com.freddieptf.mangatest.mainUi.fragments.DownloadsFragment;
import com.freddieptf.mangatest.mainUi.fragments.MangaListFragment;
import com.freddieptf.mangatest.mainUi.fragments.MyMangaFragment;
import com.freddieptf.mangatest.sync.MangaTestSyncAdapter;
import com.freddieptf.mangatest.utils.MyColorUtils;
import com.freddieptf.mangatest.utils.ThemeUtilities;
import com.freddieptf.mangatest.utils.Utilities;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


public class MainActivity extends BaseActivity implements FragmentManager.OnBackStackChangedListener,
        MainActivityHelper {

    final public static String ACTION_UPDATE = "update";
    public static DrawerLayout drawerLayout;
    FrameLayout navbarFrame;
    public static LinearLayout toolbarBig;
    String[] fragmentTitles;
    Fragment currentFrag;
    Toolbar toolbar;
    View toolBarShadow;
    public static boolean DEBUG = true;
    final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected boolean hasNavDrawer() {
        return true;
    }

    public static TextSwitcher primaryToolbarMangaCount;
    public static LinearLayout toolBarExtra;


    private ViewSwitcher.ViewFactory mFactory = new ViewSwitcher.ViewFactory() {
        @Override
        public View makeView() {
            TextView textView = new TextView(MainActivity.this);
            textView.setGravity(Gravity.TOP | Gravity.CENTER_VERTICAL);
            textView.setTextColor(getResources().getColor(android.R.color.white));
            return textView;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MangaTestSyncAdapter.initializeSyncAdapter(this);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        toolbarBig = (LinearLayout)findViewById(R.id.appBarLayout);
        toolBarShadow = findViewById(R.id.toolBarShadow);
        fragmentTitles = getResources().getStringArray(R.array.drawer_list);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        navbarFrame = (FrameLayout)findViewById(R.id.navdrawer_frame);
        toolBarExtra = (LinearLayout)findViewById(R.id.toolbar_extra);

        primaryToolbarMangaCount = (TextSwitcher)findViewById(R.id.toolbar_MangaCount);
        primaryToolbarMangaCount.setFactory(mFactory);

        Animation in = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out);

        primaryToolbarMangaCount.setInAnimation(in);
        primaryToolbarMangaCount.setOutAnimation(out);

        toolbar = (Toolbar)findViewById(R.id.toolbar_actionBar);
        toolbarBig.setBackgroundColor(new MyColorUtils(this).getPrimaryColor());
        toolbar.setPopupTheme(new ThemeUtilities(this).getPopUpTheme());
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        setSupportActionBar(toolbar);


        int navBarFrameMargin = getResources().getDimensionPixelSize(R.dimen.myNavBarMargin);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int navBarWidthLimit = getResources().getDimensionPixelSize(R.dimen.myNavBarMaxWidth);
        int navBarWidth = metrics.widthPixels - navBarFrameMargin;
        if(navBarWidth > navBarWidthLimit) navBarWidth = navBarWidthLimit;
        navbarFrame.setLayoutParams(new DrawerLayout.LayoutParams(navBarWidth, DrawerLayout.LayoutParams.MATCH_PARENT, Gravity.START));
//        navbarFrame.setBackgroundColor(getResources().getColor(R.color.primary_dark));
        navbarFrame.setBackgroundColor(Color.GRAY);
        drawerLayout.setStatusBarBackgroundColor(new MyColorUtils(this).getPrimaryDarkColor());

        setDrawerUp();

        try {

            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                File httpCacheDir = new File(this.getCacheDir(), "http");
                long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
                HttpResponseCache.install(httpCacheDir, httpCacheSize);

            }else {

                File httpCacheDir = new File(this.getCacheDir(), "http");
                long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
                Class.forName("android.net.http.HttpResponseCache")
                        .getMethod("install", File.class, long.class)
                        .invoke(null, httpCacheDir, httpCacheSize);
            }
        } catch (IOException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException
                | IllegalAccessException e) {
            e.printStackTrace();
        }



    }


    @Override
    protected void onStop() {
        super.onStop();
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if(cache != null) cache.flush();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(getSupportFragmentManager().getBackStackEntryCount() != 0){
            Utilities.Log(LOG_TAG, "BackStack count: " + getSupportFragmentManager().getBackStackEntryCount());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manga_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        switch (id) {
            case android.R.id.home: {

                if(drawerLayout.getDrawerLockMode(GravityCompat.START) == DrawerLayout.LOCK_MODE_LOCKED_CLOSED){
                    onBackPressed();
                }else {
                    boolean drawerOpen = drawerLayout.isDrawerOpen(navbarFrame);
                    if (!drawerOpen) {
                        drawerLayout.openDrawer(GravityCompat.START);
                    }

                }

                return true;
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(savedInstanceState == null && !getIntent().getAction().equals(ACTION_UPDATE)) selectItem(1);
        else if(getIntent().getAction().equals(ACTION_UPDATE)) selectItem(0);
        else if(savedInstanceState != null) Utilities.Log(LOG_TAG, "onPostCreate: savedInstance not null");
        else Utilities.Log(LOG_TAG, "onPostCreate: Unsupported Operation");

//        if(getIntent().getAction().equals(ACTION_UPDATE)) selectItem(0);

    }


    @Override
    public void onBackStackChanged() {
        try {
            Utilities.Log(LOG_TAG, "BackStack count: " + getSupportFragmentManager().getBackStackEntryCount());
            Utilities.Log(LOG_TAG, " " + getSupportFragmentManager().getBackStackEntryAt(0));
        }catch (IndexOutOfBoundsException | NullPointerException e){
            Utilities.Log(LOG_TAG, e.getMessage());
        }
    }

    @Override
    public void lockDrawer(boolean lock) {
        if(lock) drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        else drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public boolean isDrawerLocked() {
        return drawerLayout.getDrawerLockMode(GravityCompat.START) != DrawerLayout.LOCK_MODE_UNLOCKED;
    }

    @Override
    public Toolbar getToolBar() {
        return toolbar;
    }

    @Override
    public void hideToolBarShadow(boolean hide) {
        if(!hide){
            toolBarShadow.setAlpha(0f);
            toolBarShadow.setVisibility(View.VISIBLE);
            toolBarShadow.animate().alpha(1f).setDuration(200);
        }
        else {
            toolBarShadow.animate().alpha(0f).setDuration(200);
            toolBarShadow.setVisibility(View.GONE);

        }

    }

    @Override
    public boolean isToolBarShadowVisible() {
        return toolBarShadow.isShown();
    }

    @Override
    public MainActivityHelper getMainActivityHelper() {
        return this;
    }

    public void setDrawerUp(){
        final ActionBarDrawerToggle drawerToggle
                = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open,
                R.string.drawer_close){

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }

        };

        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
                drawerToggle.onDrawerSlide(drawerLayout, 0f);
            }
        });

        drawerLayout.setDrawerListener(drawerToggle);

    }

    public void recreateDownloadsFragmentWithArgs(File[] chapters, String mangaName){

        Bundle bundle = new Bundle();
        String[] filesStringArray = new String[chapters.length];

        for(int i = 0; i < chapters.length; i++){
            filesStringArray[i] = chapters[i].getAbsolutePath();
        }

        bundle.putStringArray(DownloadsFragment.CHAPTERS, filesStringArray);
        bundle.putString(DownloadsFragment.MANGA_NAME, mangaName);

        DownloadsFragment downloadsFragment =  new DownloadsFragment();
        downloadsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, downloadsFragment).addToBackStack("downloads_b").commit();

    }

    public void selectItem(int position){

        switch (position){
            case 0: {
                currentFrag = new MyMangaFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, currentFrag, fragmentTitles[position]).commit();
                break;
            }

            case 1: {
                currentFrag = new MangaListFragment();
                Bundle bundle = new Bundle();
                String sortOrder = null;
                bundle.putString(MangaListFragment.SORT_ORDER, sortOrder);
                currentFrag.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, currentFrag, fragmentTitles[position]).commit();
                break;
            }

            case 2: {
                currentFrag = new DownloadsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, currentFrag, fragmentTitles[position]).commit();
                break;
            }

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        setTitle(fragmentTitles[position]);

    }


}





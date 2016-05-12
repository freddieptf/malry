package com.freddieptf.mangatest.mainUi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.mainUi.fragments.DownloadsFragment;
import com.freddieptf.mangatest.mainUi.fragments.ListsFragment;
import com.freddieptf.mangatest.mainUi.fragments.MyMangaFragment;
import com.freddieptf.mangatest.utils.MyColorUtils;
import com.freddieptf.mangatest.utils.Utilities;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.io.File;


public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener,
        MainActivityHelper {

    final public static String ACTION_UPDATE = "update";
    //static..not sure if good or bad
    public static AppBarLayout toolbarBig;
    public static boolean DEBUG = true;
    final String LOG_TAG = this.getClass().getSimpleName();
    String[] fragmentTitles;
    Fragment currentFrag;
    Toolbar toolbar;
    TabLayout tabLayout;
    View toolBarShadow;
    BottomBar bottomBar;
    int position = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().addOnBackStackChangedListener(this);

        toolbarBig = (AppBarLayout)findViewById(R.id.appBarLayout);
        toolBarShadow = findViewById(R.id.toolBarShadow);
        fragmentTitles = getResources().getStringArray(R.array.drawer_list);
        toolbar = (Toolbar)findViewById(R.id.toolbar_actionBar);
        toolbarBig.setBackgroundColor(new MyColorUtils(this).getPrimaryColor());
        toolbar.setPopupTheme(R.style.ThemeOverlay_AppCompat_Light);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        setUpBottomBar(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bottomBar.onSaveInstanceState(outState);
    }

    private void setUpBottomBar(Bundle savedInstanceState) {
        bottomBar = BottomBar.attachShy(
                (CoordinatorLayout) findViewById(R.id.coordinator),
                findViewById(R.id.list),
                savedInstanceState);
        bottomBar.setItemsFromMenu(R.menu.bottombar_menu, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                selectItem(menuItemId);
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
            }
        });
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
                onBackPressed();
                return true;
            }
            case R.id.action_settings:
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
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
    public void hideBottomBar(boolean hide) {

    }

    @Override
    public Toolbar getToolBar() {
        return toolbar;
    }

    @Override
    public TabLayout getTabs() {
        return tabLayout;
    }

    @Override
    public MainActivityHelper getMainActivityHelper() {
        return this;
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

    public void selectItem(int id) {
        switch (id) {
            case R.id.menu_frag_mylibrary: {
                position = 0;
                currentFrag = new MyMangaFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, currentFrag, fragmentTitles[position]).commit();
                break;
            }

            case R.id.menu_frag_lists: {
                position = 1;
                currentFrag = new ListsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, currentFrag, fragmentTitles[position]).commit();
                break;
            }

            case R.id.menu_frag_download: {
                position = 2;
                currentFrag = new DownloadsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, currentFrag, fragmentTitles[position]).commit();
                break;
            }

        }
        setTitle(fragmentTitles[position]);
    }

}





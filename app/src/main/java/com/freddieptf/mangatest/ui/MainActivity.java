package com.freddieptf.mangatest.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.manga.details.MangaDetailsListLoader;
import com.freddieptf.mangatest.data.manga.details.MangaDetailsRepository;
import com.freddieptf.mangatest.data.manga.lists.MangaListLoader;
import com.freddieptf.mangatest.data.sync.SyncHelper;
import com.freddieptf.mangatest.ui.base.BaseActivity;
import com.freddieptf.mangatest.ui.downloads.DownloadsFragment;
import com.freddieptf.mangatest.ui.library.LibraryFragment;
import com.freddieptf.mangatest.ui.library.LibraryPresenter;
import com.freddieptf.mangatest.ui.lists.ListFragment;
import com.freddieptf.mangatest.ui.lists.ListPresenter;
import com.freddieptf.mangatest.utils.MyColorUtils;
import com.freddieptf.mangatest.utils.Utilities;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.io.File;


public class MainActivity extends BaseActivity {

    final public static String ACTION_UPDATE = "update";
    public static boolean DEBUG = true;
    final String LOG_TAG = this.getClass().getSimpleName();
    String[] fragmentTitles;
    Toolbar toolbar;
    TabLayout tabLayout;
    View toolBarShadow;
    BottomBar bottomBar;
    int position = -1;
    private AppBarLayout toolbarBig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SyncHelper.scheduleJobs();

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
    public TabLayout getTabs() {
        return tabLayout;
    }

    @Override
    public void setNavigationIcon(int resId) {
        toolbar.setNavigationIcon(resId);
    }

    @Override
    public void hideTabs() {
        tabLayout.setVisibility(View.GONE);
    }

    @Override
    public void showTabs() {
        tabLayout.setVisibility(View.VISIBLE);
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

                LibraryFragment libraryFragment = (LibraryFragment) getSupportFragmentManager()
                        .findFragmentByTag(fragmentTitles[position]);

                if (libraryFragment == null) libraryFragment = new LibraryFragment();

                getSupportFragmentManager().beginTransaction().replace(R.id.container, libraryFragment,
                        fragmentTitles[position]).commit();

                MangaDetailsRepository repository = MangaDetailsRepository.getInstance();
                MangaDetailsListLoader loader = new MangaDetailsListLoader(this, repository);
                LibraryPresenter libraryPresenter = new LibraryPresenter(getSupportLoaderManager(), loader, repository, libraryFragment);

                break;
            }

            case R.id.menu_frag_lists: {
                position = 1;

                ListFragment listFragment = (ListFragment) getSupportFragmentManager().findFragmentByTag(fragmentTitles[position]);
                if (listFragment == null) listFragment = new ListFragment();

                getSupportFragmentManager().beginTransaction().replace(R.id.container, listFragment, fragmentTitles[position]).commit();

                MangaListLoader loader = (MangaListLoader) getSupportLoaderManager().<MangaListLoader.MangaLists>getLoader(ListPresenter.LOADER_ID);
                if (loader == null) loader = new MangaListLoader(this);
                ListPresenter presenter = new ListPresenter(getSupportLoaderManager(), loader, listFragment);
                break;
            }

            case R.id.menu_frag_download: {
                position = 2;
                DownloadsFragment downloadsFragment = new DownloadsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, downloadsFragment, fragmentTitles[position]).commit();
                break;
            }

        }
        setTitle(fragmentTitles[position]);
    }

}





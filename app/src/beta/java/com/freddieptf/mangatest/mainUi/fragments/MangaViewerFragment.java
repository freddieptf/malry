package com.freddieptf.mangatest.mainUi.fragments;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.adapters.PicPagerAdapter;
import com.freddieptf.mangatest.mainUi.widgets.CustomViewPager;
import com.freddieptf.mangatest.utils.Utilities;

import java.io.IOException;

/**
 * Created by fred on 3/22/15.
 */
public class MangaViewerFragment extends Fragment {

    final String LOG_TAG = getClass().getSimpleName();
    public static final String PIC_URLS = "pic_urls";
    public static final String MANGA_TITLE = "manga_title";
    public static final String CHAPTER_TITLE = "chapter_title";
    final String SCROLL_POSITION = "pos";
    String[] picUris;
    PicPagerAdapter adapter;
    ActionBar actionBar;
    int pos = 0;
    int pageCount = -1;
    CustomViewPager viewPager;
    String chapterTitle;
    WallpaperManager wallpaperManager;

    public MangaViewerFragment(){
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getActivity().getIntent().getBundleExtra("bundle");
        chapterTitle = bundle.getString("chapter_title");
        if(getArguments() != null && getArguments().getInt("pos") != 0) pos = getArguments().getInt("pos");
        Utilities.Log(LOG_TAG, "pos: " + pos + " chpterName: " + chapterTitle);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manga_viewer, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

        Bundle bundle = getActivity().getIntent().getBundleExtra("bundle");
        picUris = bundle.getStringArray(PIC_URLS);
        chapterTitle = bundle.getString("chapter_title");
        String mangaTitle = bundle.getString("manga_title");

        if(actionBar != null) actionBar.setSubtitle(chapterTitle);
        Utilities.Log(LOG_TAG, " picUris " + picUris.length);

        pageCount = picUris.length;

        viewPager = (CustomViewPager)view.findViewById(R.id.pager_MangaPics);

        adapter = new PicPagerAdapter(getActivity(), picUris, true, viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(pos, false);

        wallpaperManager = WallpaperManager.getInstance(getActivity());

//        Utilities.changeSystemUiOnTap(viewPager, getActivity());
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Utilities.hideSystemUi(viewPager);
//            }
//        }, 1750);

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_WALLPAPER_CHANGED));
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(broadcastReceiver);

        if((pageCount - 1) != viewPager.getCurrentItem() && viewPager.getCurrentItem() != 0) {
            Utilities.writeMangaPageToPrefs(getActivity(),
                    chapterTitle,
                    viewPager.getCurrentItem());
        }else{
            Utilities.writeMangaPageToPrefs(getActivity(),
                    chapterTitle,
                    0);
        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_viewer, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_wallpaper:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            wallpaperManager.setBitmap(BitmapFactory.decodeFile(adapter.getCurrentPicUri()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Snackbar.make(viewPager, "Wallpaper set", Snackbar.LENGTH_LONG).show();
        }
    };
}

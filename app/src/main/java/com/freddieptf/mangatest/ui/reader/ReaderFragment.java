package com.freddieptf.mangatest.ui.reader;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.model.ChapterPages;
import com.freddieptf.mangatest.data.model.ImagePage;
import com.freddieptf.mangatest.ui.widgets.CustomViewPager;
import com.freddieptf.mangatest.utils.Utilities;

import java.io.IOException;

/**
 * Created by fred on 3/22/15.
 */
public class ReaderFragment extends Fragment {

    public static final String PIC_URLS = "pic_urls";
    public static final String MANGA_TITLE = "manga_title";
    public static final String CHAPTER_TITLE = "chapterTitle";
    private PicPagerAdapter adapter;
    private int pos = 0;
    private CustomViewPager viewPager;
    private String chapterTitle;
    private WallpaperManager wallpaperManager;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Snackbar.make(viewPager, "Wallpaper set", Snackbar.LENGTH_LONG).show();
        }
    };

    private ImagePage[] pages;

    public ReaderFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChapterPages chapterPages = getActivity().getIntent().getParcelableExtra(ReaderActivity.CHAPTER_BOII);
        chapterTitle = chapterPages.getChapterTitle();
        pages = chapterPages.getImagePages();
        if(getArguments() != null && getArguments().getInt("pos") != 0) pos = getArguments().getInt("pos");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manga_viewer, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(chapterTitle);
        }
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = (CustomViewPager)view.findViewById(R.id.pager_MangaPics);
        adapter = new PicPagerAdapter(pages);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(pos, false);
        wallpaperManager = WallpaperManager.getInstance(getActivity());
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
        if ((pages.length - 1) != viewPager.getCurrentItem() && viewPager.getCurrentItem() != 0) {
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
                            wallpaperManager.setBitmap(BitmapFactory.decodeFile(adapter.getCurrentPicUri(viewPager.getCurrentItem())));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

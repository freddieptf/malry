package com.freddieptf.mangatest.mainUi.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.adapters.DownloadsPagerAdapter;
import com.freddieptf.mangatest.adapters.FilesAdapter;
import com.freddieptf.mangatest.mainUi.MainActivity;
import com.freddieptf.mangatest.mainUi.MangaViewerActivity;
import com.freddieptf.mangatest.mainUi.baseUi.BaseFragment;
import com.freddieptf.mangatest.recyclerviewdecor.swipestuff.ItemDismissedHelper;
import com.freddieptf.mangatest.recyclerviewdecor.swipestuff.ItemTouchHelperCallback;

import java.io.File;

/**
 * Created by fred on 2/23/15.
 */
public class DownloadsFragment extends BaseFragment implements FilesAdapter.SwipeListener,
        DownloadsPagerAdapter.DonwloadsPagerHelper {

    FilesAdapter filesAdapter;
    String[] filePaths;
    String mangaName;
    public final static String CHAPTERS = "chapters";
    public static final String MANGA_NAME = "name";
    public final String LOG_TAG = getClass().getSimpleName();
    ItemDismissedHelper itemDismissedHelper;
    boolean lockdrawer;
    ViewPager viewPager;
    ItemTouchHelper itemTouchHelper;

    @Override
    protected boolean showTabs() {
        return !lockdrawer;
    }

    @Override
    protected boolean lockDrawer() {
        return lockdrawer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView  = inflater.inflate(R.layout.fragment_donwloads, container, false);

        if(getArguments() != null){
            lockdrawer = true;
            getMainActivityHelper().getToolBar().setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

            //chapter files string paths
            filePaths = getArguments().getStringArray(CHAPTERS);
            mangaName = getArguments().getString(MANGA_NAME);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mangaName);

        }else {
            lockdrawer = false;
            getMainActivityHelper().getToolBar().setNavigationIcon(R.drawable.ic_menu_white_24dp);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.myDownloads));
        }

        return rootView;

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String parentDirectory = Environment.getExternalStorageDirectory().toString();
        File myMangaParent = new File(parentDirectory + "/MangaTest");
        final File[] mangaArray;

        if(filePaths != null && filePaths.length > 0){
            //if not null, show this(chapters) in listview
            mangaArray = new File[filePaths.length];

            for(int i = 0; i < filePaths.length; i++){
                mangaArray[i] = new File(filePaths[i]);
            }

            filesAdapter = new FilesAdapter(getActivity(), mangaArray, false, new FilesAdapter.ClickListener() {
                @Override
                public void onClick(int index) {
                    File file = mangaArray[index];
                    String[] picUrls = new String[file.listFiles().length];
                    for (int y = 0; y < file.listFiles().length; y++){
                        picUrls[y] = file.listFiles()[y].getAbsolutePath();

                    }

                    Bundle bundle = new Bundle();
                    bundle.putStringArray(MangaViewerFragment.PIC_URLS, picUrls);
                    bundle.putString(MangaViewerFragment.CHAPTER_TITLE, file.getName());
                    bundle.putString(MangaViewerFragment.MANGA_TITLE, mangaName);

                    Intent intent =  new Intent(getActivity(), MangaViewerActivity.class);
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);

                }
            }, this);

        }else {
            //show mangas in parent folder
            if(myMangaParent.exists()){
                mangaArray = myMangaParent.listFiles();
                filesAdapter = new FilesAdapter(getActivity(), mangaArray, true, new FilesAdapter.ClickListener() {
                    @Override
                    public void onClick(int index) {
                        File file = mangaArray[index];
                        File[] chapters = file.listFiles();
                        //recreate fragment with chapters in listview
                        ((MainActivity) getActivity()).recreateDownloadsFragmentWithArgs(chapters, file.getName());
                    }
                }, this);
            }

        }

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(filesAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);

        TabLayout tabLayout = getMainActivityHelper().getTabs();
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setAdapter(new DownloadsPagerAdapter(this));
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public void getRecyclerView(int position, RecyclerView recyclerView) {
        switch (position){
            case 0:
                recyclerView.setAdapter(filesAdapter);
                itemTouchHelper.attachToRecyclerView(recyclerView);
                break;
            case 1:
                break;
        }
    }

    @Override
    public void onSwipeToDelete(File file, ItemDismissedHelper itemDismissedHelper) {
        this.itemDismissedHelper = itemDismissedHelper;
    }


}

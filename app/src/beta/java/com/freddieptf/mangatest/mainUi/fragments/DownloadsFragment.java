package com.freddieptf.mangatest.mainUi.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.adapters.FilesAdapter;
import com.freddieptf.mangatest.mainUi.MainActivity;
import com.freddieptf.mangatest.mainUi.MangaViewerActivity;
import com.freddieptf.mangatest.mainUi.baseUi.BaseFragment;
import com.freddieptf.mangatest.recyclerviewdecor.DividerItemDecoration;
import com.freddieptf.mangatest.recyclerviewdecor.swipestuff.ItemDismissedHelper;
import com.freddieptf.mangatest.recyclerviewdecor.swipestuff.ItemTouchHelperCallback;

import java.io.File;

/**
 * Created by fred on 2/23/15.
 */
public class DownloadsFragment extends BaseFragment implements FilesAdapter.SwipeListener {

    FilesAdapter filesAdapter;
    String[] filePaths;
    String mangaName;
    public final static String CHAPTERS = "chapters";
    public static final String MANGA_NAME = "name";
    public final String LOG_TAG = getClass().getSimpleName();
    ItemDismissedHelper itemDismissedHelper;
    boolean lockdrawer;
    ItemTouchHelper itemTouchHelper;

    @Override
    protected boolean lockDrawer() {
        return lockdrawer;
    }

    @Override
    protected int useNavigationIcon() {
        if(lockdrawer) return R.drawable.abc_ic_ab_back_mtrl_am_alpha;
        return super.useNavigationIcon();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView  = inflater.inflate(R.layout.fragment_donwloads, container, false);

        if(getArguments() != null){
            lockdrawer = true;
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

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(filesAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    @Override
    public void onSwipeToDelete(File file, ItemDismissedHelper itemDismissedHelper) {
        this.itemDismissedHelper = itemDismissedHelper;
    }


}

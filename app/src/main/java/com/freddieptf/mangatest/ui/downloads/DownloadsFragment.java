package com.freddieptf.mangatest.ui.downloads;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.ui.MainActivity;
import com.freddieptf.mangatest.ui.base.BaseFragment;
import com.freddieptf.mangatest.ui.recyclerviewdecor.DividerItemDecoration;
import com.freddieptf.mangatest.ui.recyclerviewdecor.swipestuff.ItemDismissedHelper;
import com.freddieptf.mangatest.ui.recyclerviewdecor.swipestuff.ItemTouchHelperCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fred on 2/23/15.
 */
public class DownloadsFragment extends BaseFragment implements FilesAdapter.SwipeListener {

    public final static String CHAPTERS = "chapters";
    public static final String MANGA_NAME = "name";
    public final String LOG_TAG = getClass().getSimpleName();
    FilesAdapter filesAdapter;
    String[] filePaths;
    String mangaName;
    RecyclerView recyclerView;
    ItemDismissedHelper itemDismissedHelper;
    boolean hidebottombar;
    ItemTouchHelper itemTouchHelper;
    List<File> toDelete;

    @Override
    protected int useNavigationIcon() {
        if (hidebottombar) return R.drawable.ic_action_name;
        return super.useNavigationIcon();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView  = inflater.inflate(R.layout.fragment_donwloads, container, false);

        if(getArguments() != null){
            hidebottombar = true;
            //chapter files string paths
            filePaths = getArguments().getStringArray(CHAPTERS);
            mangaName = getArguments().getString(MANGA_NAME);
//            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mangaName);

        }else {
            hidebottombar = false;
//            getMainActivityHelper().getToolBar().setNavigationIcon(R.drawable.ic_stat_maps_local_library);
//            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.myDownloads));
        }

        return rootView;

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toDelete = new ArrayList<>();

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

//                    Bundle bundle = new Bundle();
//                    bundle.putStringArray(MangaViewerFragment.PIC_URLS, picUrls);
//                    bundle.putString(MangaViewerFragment.CHAPTER_TITLE, file.getName());
//                    bundle.putString(MangaViewerFragment.MANGA_TITLE, mangaName);
//
//                    Intent intent =  new Intent(getActivity(), MangaViewerActivity.class);
//                    intent.putExtra("bundle", bundle);
//                    startActivity(intent);

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

        //@TODO fuck this..NOPE
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(filesAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(filesAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    @Override
    public void onSwipeToDelete(final File file, final ItemDismissedHelper itemDismissedHelper) {
        this.itemDismissedHelper = itemDismissedHelper;
        Snackbar.make(recyclerView, "Item deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemDismissedHelper.onUndoDismiss();
                    }
                })
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        if (event == DISMISS_EVENT_TIMEOUT) {
                            toDelete.add(file);
                            delete();
                        }
                    }
                })
                .show();
    }

    //meh..the deepest we can go is 2 anyway
    public void delete(){
        if(toDelete != null && toDelete.size() >0) {
            for (File file : toDelete) {

                if (file != null && file.isDirectory() && file.listFiles().length > 0) {
                    for (File f : file.listFiles()) {

                        if (f.isDirectory()) {
                            for (File child : f.listFiles()) child.delete();
                            f.delete();
                        }
                        else f.delete();
                    }

                    file.delete();

                } else {
                    if (file != null) file.delete();
                }

            }
        }
    }


}

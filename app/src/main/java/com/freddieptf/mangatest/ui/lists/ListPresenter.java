package com.freddieptf.mangatest.ui.lists;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.freddieptf.mangatest.data.manga.lists.MangaListLoader;

/**
 * Created by freddieptf on 11/10/16.
 */

public class ListPresenter implements LoaderManager.LoaderCallbacks<MangaListLoader.MangaLists> {

    private MangaListView view;
    private MangaListLoader loader;
    private LoaderManager loaderManager;

    public ListPresenter(LoaderManager loaderManager, MangaListLoader loader, MangaListView view){
        this.loader = loader;
        this.loaderManager = loaderManager;
        this.view = view;

        view.setPresenter(this);
    }

    public void init(){
        loaderManager.initLoader(23223, null, this);
    }

    @Override
    public Loader<MangaListLoader.MangaLists> onCreateLoader(int id, Bundle args) {
        view.showProgress(true);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<MangaListLoader.MangaLists> loader, MangaListLoader.MangaLists data) {
        view.onDataLoad(data);
        view.showProgress(false);
    }

    @Override
    public void onLoaderReset(Loader<MangaListLoader.MangaLists> loader) {

    }
}

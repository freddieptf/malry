package com.freddieptf.mangatest.ui.lists;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.freddieptf.mangatest.data.manga.lists.MangaListLoader;

/**
 * Created by freddieptf on 11/10/16.
 */

public class ListPresenter implements LoaderManager.LoaderCallbacks<MangaListLoader.MangaLists> {

    public static final int LOADER_ID = 23223;
    private ListView view;
    private MangaListLoader loader;
    private LoaderManager loaderManager;

    public ListPresenter(LoaderManager loaderManager, MangaListLoader loader, ListView view) {
        this.loader = loader;
        this.loaderManager = loaderManager;
        this.view = view;
        view.setPresenter(this);
    }

    public void init(){
        loaderManager.initLoader(LOADER_ID, null, this);
    }

    public void switchSource(String source) {
        loader.SWITCH_IT_UP(source);
    }

    public String getActiveSource() {
        return loader.getActiveSource();
    }

    public void setActiveSource(String source) {
        loader.setActiveSource(source);
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

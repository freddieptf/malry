package com.freddieptf.mangatest.ui.library;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.freddieptf.mangatest.data.manga.details.MangaDetailsListLoader;
import com.freddieptf.mangatest.data.manga.details.MangaDetailsRepository;
import com.freddieptf.mangatest.data.model.MangaDetails;

import java.util.ArrayList;

/**
 * Created by freddieptf on 27/09/16.
 */
public class LibraryPresenter implements LoaderManager.LoaderCallbacks<ArrayList<MangaDetails>> {

    private LibraryView libraryView;
    private LoaderManager loaderManager;
    private MangaDetailsRepository repository;
    private MangaDetailsListLoader mangaDetailsListLoader;

    public LibraryPresenter(LoaderManager loaderManager, MangaDetailsListLoader loader,
                            MangaDetailsRepository repository, LibraryView libraryView) {

        this.libraryView = libraryView;
        mangaDetailsListLoader = loader;
        this.repository = repository;
        this.loaderManager = loaderManager;
        libraryView.setPresenter(this);
    }

    public void start() {
        loaderManager.initLoader(232, null, this);
    }

    @Override
    public Loader<ArrayList<MangaDetails>> onCreateLoader(int id, Bundle args) {
        return mangaDetailsListLoader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MangaDetails>> loader, ArrayList<MangaDetails> data) {
        if (data == null || data.isEmpty()) libraryView.showEmptyView();
        else libraryView.onDataLoad(data);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MangaDetails>> loader) {

    }

    public void deleteItem(String name, int position) {
        repository.deleteMangaDetails(name);
        libraryView.onItemDeleted(name, position);
    }
}

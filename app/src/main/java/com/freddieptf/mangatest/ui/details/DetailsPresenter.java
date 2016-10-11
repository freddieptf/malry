package com.freddieptf.mangatest.ui.details;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.freddieptf.mangatest.data.manga.details.MangaDetailsLoader;
import com.freddieptf.mangatest.data.manga.details.MangaDetailsRepository;
import com.freddieptf.mangatest.data.model.MangaDetails;

/**
 * Created by freddieptf on 28/09/16.
 */

public class DetailsPresenter implements LoaderManager.LoaderCallbacks<MangaDetails> {

    private LoaderManager loaderManager;
    private MangaDetailsLoader loader;
    private MangaDetailsRepository repository;
    private DetailsView detailsView;
    private MangaDetails mangaDetails;

    public DetailsPresenter(LoaderManager loaderManager, MangaDetailsLoader loader,
                            MangaDetailsRepository repository, DetailsView detailsView) {
        this.loaderManager = loaderManager;
        this.loader = loader;
        this.repository = repository;
        this.detailsView = detailsView;
        detailsView.setPresenter(this);
    }

    public void start() {
        loaderManager.initLoader(4556, null, this);
        detailsView.showProgress(mangaDetails == null);
    }

    @Override
    public Loader<MangaDetails> onCreateLoader(int id, Bundle args) {
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<MangaDetails> loader, MangaDetails data) {
        mangaDetails = data;
        detailsView.onDataLoad(data);
        detailsView.showProgress(false);
    }

    @Override
    public void onLoaderReset(Loader<MangaDetails> loader) {

    }

    public void save() {
        repository.saveMangaDetails(mangaDetails, loader.getMangaId(), loader.getSource());
        detailsView.onDataSaved();
    }
}

package com.freddieptf.mangatest.ui.detailView;

import com.freddieptf.mangatest.data.model.MangaDetails;

/**
 * Created by freddieptf on 28/09/16.
 */

public interface DetailsView {
    void setPresenter(DetailsPresenter detailsPresenter);

    void onDataLoad(MangaDetails mangaDetails);

    void showProgress(boolean show);

    void onDataSaved();

    void onError();
}

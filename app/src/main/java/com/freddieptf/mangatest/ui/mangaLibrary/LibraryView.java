package com.freddieptf.mangatest.ui.mangaLibrary;

import com.freddieptf.mangatest.data.model.MangaDetails;

import java.util.ArrayList;

/**
 * Created by freddieptf on 27/09/16.
 */

public interface LibraryView {
    void showEmptyView();

    void onDataLoad(ArrayList<MangaDetails> mangaDetailsArrayList);

    void setPresenter(LibraryPresenter libraryPresenter);

    void onItemDeleted(String name, int position);
}

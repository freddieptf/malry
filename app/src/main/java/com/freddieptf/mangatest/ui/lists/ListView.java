package com.freddieptf.mangatest.ui.lists;

import com.freddieptf.mangatest.data.manga.lists.MangaListLoader;

/**
 * Created by freddieptf on 11/10/16.
 */

public interface ListView {
    void setPresenter(ListPresenter listPresenter);
    void onDataLoad(MangaListLoader.MangaLists mangaLists);
    void showProgress(boolean show);
}

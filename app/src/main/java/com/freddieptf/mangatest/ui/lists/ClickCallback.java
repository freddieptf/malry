package com.freddieptf.mangatest.ui.lists;

import com.freddieptf.mangatest.data.model.LatestMangaItem;
import com.freddieptf.mangatest.data.model.MangaItem;

/**
 * Created by freddieptf on 11/10/16.
 */

public interface ClickCallback {

    void onMangaItemClick(String source, MangaItem item);

    void onLatestMangaItemClick(LatestMangaItem item);

    void onPopularMangaItemClick();
}

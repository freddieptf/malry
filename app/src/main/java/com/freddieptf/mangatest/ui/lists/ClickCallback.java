package com.freddieptf.mangatest.ui.lists;

import com.freddieptf.mangatest.data.model.LatestMangaItem;
import com.freddieptf.mangatest.data.model.MangaItem;
import com.freddieptf.mangatest.data.model.PopularMangaItem;

/**
 * Created by freddieptf on 11/10/16.
 */

public interface ClickCallback {

    void onMangaItemClick(String source, MangaItem item);

    void onLatestMangaItemClick(LatestMangaItem item);

    void onPopularMangaItemClick(PopularMangaItem popularMangaItem);
}

package com.freddieptf.mangatest.data.manga.lists;

import android.content.Context;

import com.freddieptf.mangatest.data.model.LatestMangaItem;
import com.freddieptf.mangatest.data.model.MangaItem;
import com.freddieptf.mangatest.data.model.PopularMangaItem;

import java.util.ArrayList;

/**
 * Created by freddieptf on 11/10/16.
 */

public interface MangaListSource {

    ArrayList<MangaItem> getMangaReaderMangaList(Context context);

    ArrayList<MangaItem> getMangaFoxMangaList(Context context);

    ArrayList<LatestMangaItem> getMangaReaderLatestList(Context context);

    ArrayList<PopularMangaItem> getMangaReaderPopularList(Context context);
}

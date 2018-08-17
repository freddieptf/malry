package com.freddieptf.mangatest.data.manga.lists;

import android.content.Context;

import com.freddieptf.mangatest.data.model.LatestMangaItem;
import com.freddieptf.mangatest.data.model.MangaItem;
import com.freddieptf.mangatest.data.model.PopularMangaItem;
import com.freddieptf.mangatest.data.remote.MangaFox;
import com.freddieptf.mangatest.data.remote.MangaReader;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by freddieptf on 11/10/16.
 */

public class MangaListRemoteSource implements MangaListSource {

    private static MangaListRemoteSource INSTANCE;
    private MangaReader mangaReader;
    private MangaFox mangaFox;

    private MangaListRemoteSource() {
        mangaReader = MangaReader.getInstance();
        mangaFox = MangaFox.getInstance();
    }

    public static MangaListRemoteSource getInstance() {
        if (INSTANCE == null) INSTANCE = new MangaListRemoteSource();
        return INSTANCE;
    }

    @Override
    public ArrayList<MangaItem> getMangaReaderMangaList(Context context) {
        try {
            return mangaReader.getMangaList();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ArrayList<MangaItem> getMangaFoxMangaList(Context context) {
        try {
            return mangaFox.getMangaList();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ArrayList<LatestMangaItem> getMangaReaderLatestList(Context context) {
        try {
            return mangaReader.getLatestList();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ArrayList<PopularMangaItem> getMangaReaderPopularList(Context context) {
        try {
            return mangaReader.getPopularList();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

package com.freddieptf.mangatest.data.manga.lists;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.model.LatestMangaItem;
import com.freddieptf.mangatest.data.model.MangaItem;
import com.freddieptf.mangatest.data.model.PopularMangaItem;

import java.util.ArrayList;

/**
 * Created by freddieptf on 11/10/16.
 */

public class MangaListLoader extends AsyncTaskLoader<MangaListLoader.MangaLists> {

    private static final String TAG = "MangaListLoader";
    private MangaListRepository repository;
    private boolean SWITCH_IT_UP = false; //false for MangaReader, true for MangaFox.
    private String source = "";

    public MangaListLoader(Context context) {
        super(context);
        repository = MangaListRepository.getInstance();
    }

    public void SWITCH_IT_UP(String source) {
        this.source = source;
        SWITCH_IT_UP = source.equals(getContext().getString(R.string.pref_manga_fox));
        forceLoad();
    }

    public String getActiveSource() {
        return source;
    }

    public void setActiveSource(String source) {
        this.source = source;
    }

    @Override
    public MangaLists loadInBackground() {
        Log.d(TAG, "loadInBackground");
        MangaLists lists = new MangaLists(SWITCH_IT_UP ? repository.getMangaFoxMangaList(getContext()) : repository.getMangaReaderMangaList(getContext()));
        lists.setLatestItems(repository.getMangaReaderLatestList(getContext()))
                .setPopularItems(repository.getMangaReaderPopularList(getContext()));
        return lists;
    }

    @Override
    public void deliverResult(MangaLists data) {
        Log.d(TAG, "deliverResults");
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading");
        if (SWITCH_IT_UP) {
            if (repository.foxListCacheAvailable() && repository.readerListCacheAvailable()) {
                Log.d(TAG, "load from cache");
                MangaLists mangaLists = new MangaLists(repository.getFoxListCache())
                        .setLatestItems(repository.getLatestListCache());
                deliverResult(mangaLists);
            } else {
                Log.d(TAG, "force load boiii mangafox");
                forceLoad();
            }
        } else {
            if (repository.readerListCacheAvailable()) {
                Log.d(TAG, "load from cache");
                MangaLists mangaLists = new MangaLists(repository.getReaderListCache())
                        .setLatestItems(repository.getLatestListCache());
                deliverResult(mangaLists);
            } else {
                Log.d(TAG, "force load boii mangareader");
                forceLoad();
            }
        }
    }

    public static class MangaLists {
        //I AM A GAAWWWD
        private ArrayList<MangaItem> mangaItems;
        private ArrayList<LatestMangaItem> latestMangaItems;
        private ArrayList<PopularMangaItem> popularMangaItems;

        public MangaLists(ArrayList<MangaItem> mangaItems) {
            this.mangaItems = mangaItems;
        }

        public MangaLists setLatestItems(ArrayList<LatestMangaItem> latestMangaItems) {
            this.latestMangaItems = latestMangaItems;
            return this;
        }

        public MangaLists setPopularItems(ArrayList<PopularMangaItem> popularMangaItems) {
            this.popularMangaItems = popularMangaItems;
            return this;
        }

        public ArrayList<MangaItem> getMangaItems() {
            return mangaItems;
        }

        public ArrayList<LatestMangaItem> getLatestMangaItems() {
            return latestMangaItems;
        }

        public ArrayList<PopularMangaItem> getPopularMangaItems() {
            return popularMangaItems;
        }
    }
}

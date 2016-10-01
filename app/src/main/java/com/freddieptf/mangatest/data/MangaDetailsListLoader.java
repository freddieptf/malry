package com.freddieptf.mangatest.data;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.freddieptf.mangatest.data.model.MangaDetails;

import java.util.ArrayList;

/**
 * Created by freddieptf on 28/09/16.
 */

public class MangaDetailsListLoader extends AsyncTaskLoader<ArrayList<MangaDetails>>
        implements MangaDetailsRepository.RepositoryObserver {

    private static final String TAG = "MangaDetailsListLoader";
    private MangaDetailsRepository mangaDetailsRepository;

    public MangaDetailsListLoader(Context context, MangaDetailsRepository mangaDetailsRepository) {
        super(context);
        this.mangaDetailsRepository = mangaDetailsRepository;
    }

    @Override
    public ArrayList<MangaDetails> loadInBackground() {
        return mangaDetailsRepository.getMangaDetailsList();
    }

    @Override
    protected void onStartLoading() {
        if (mangaDetailsRepository.cacheAvailable())
            deliverResult(mangaDetailsRepository.getCache());
        else forceLoad();
        mangaDetailsRepository.addContentObserver(this);
    }

    @Override
    public void deliverResult(ArrayList<MangaDetails> data) {
        if (isReset()) return;
        if (isStarted()) super.deliverResult(data);
    }

    @Override
    protected void onStopLoading() {
        cancelLoadInBackground();
    }

    @Override
    public void onDataChanged() {
        if (isStarted()) forceLoad();
    }
}

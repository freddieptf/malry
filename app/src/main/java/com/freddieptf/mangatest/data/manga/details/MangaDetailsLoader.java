package com.freddieptf.mangatest.data.manga.details;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.freddieptf.mangatest.data.model.MangaDetails;

/**
 * Created by freddieptf on 28/09/16.
 */

public class MangaDetailsLoader extends AsyncTaskLoader<MangaDetails>
        implements MangaDetailsRepository.RepositoryObserver {

    private MangaDetailsRepository mangaDetailsRepository;
    private MangaDetails mangaDetails;
    private String mangaId, mangaName, source;

    public MangaDetailsLoader(Context context, MangaDetailsRepository mangaDetailsRepository) {
        super(context);
        this.mangaDetailsRepository = mangaDetailsRepository;
    }

    public void setRequestDetails(String mangaId, String mangaName, String source) {
        this.source = source;
        this.mangaName = mangaName;
        this.mangaId = mangaId;
    }

    @Override
    public MangaDetails loadInBackground() {
        mangaDetails = mangaDetailsRepository.getMangaDetails(mangaId, mangaName, source, getContext());
        return mangaDetails;
    }

    @Override
    protected void onStartLoading() {
        if (mangaDetails != null) deliverResult(mangaDetails);
        else forceLoad();
        mangaDetailsRepository.addContentObserver(this);
    }

    @Override
    protected void onStopLoading() {
        cancelLoadInBackground();
    }

    @Override
    public void onDataChanged() {
        if (isStarted()) forceLoad();
    }

    public String getMangaId() {
        return mangaId;
    }

    public String getMangaName() {
        return mangaName;
    }

    public String getSource() {
        return source;
    }
}


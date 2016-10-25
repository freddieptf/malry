package com.freddieptf.mangatest.data.manga.details;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.freddieptf.mangatest.data.model.MangaDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by freddieptf on 28/09/16.
 */

public class MangaDetailsRepository implements MangaDetailsSource {

    private static MangaDetailsRepository INSTANCE;

    private final MangaDetailsLocalSource localSource;
    private final MangaDetailsRemoteSource remoteSource;
    private List<RepositoryObserver> repositoryObservers = new ArrayList<>();

    private ArrayList<MangaDetails> cache;

    private MangaDetailsRepository() {
        localSource = new MangaDetailsLocalSource();
        remoteSource = new MangaDetailsRemoteSource();
    }

    public static MangaDetailsRepository getInstance() {
        if (INSTANCE == null) INSTANCE = new MangaDetailsRepository();
        return INSTANCE;
    }

    public boolean cacheAvailable() {
        return cache != null && !cache.isEmpty();
    }

    public void refreshRepository() {
        cache = null;
        notifyObserver();
    }

    public ArrayList<MangaDetails> getCache() {
        return cache;
    }

    @Override
    public ArrayList<MangaDetails> getMangaDetailsList(Context context) {
        cache = localSource.getMangaDetailsList(context);
        return localSource.getMangaDetailsList(context);
    }

    @Override
    public MangaDetails getMangaDetails(@Nullable String id, @Nullable String name, @NonNull String source, Context context) {
        MangaDetails mangaDetails = localSource.getMangaDetails(id, name, source, context);
        if (mangaDetails == null) {
            mangaDetails = remoteSource.getMangaDetails(id, name, source, context);
        }
        return mangaDetails;
    }

    @Override
    public void saveMangaDetails(@NonNull MangaDetails mangaDetails, @NonNull String mangaId, @NonNull String source, Context context) {
        localSource.saveMangaDetails(mangaDetails, mangaId, source, context);
        refreshRepository();
    }

    public void updateMangaDetails(Context context, String name, ContentValues contentValues) {
        localSource.updateMangaDetails(context, name, contentValues);
        refreshRepository();
    }

    @Override
    public void deleteMangaDetails(@NonNull String mangaName, Context context) {
        localSource.deleteMangaDetails(mangaName, context);
        notifyObserver();
    }

    @Override
    public void deleteAllMangaDetails(Context context) {
        localSource.deleteAllMangaDetails(context);
        notifyObserver();
    }

    public void addContentObserver(RepositoryObserver repositoryObserver) {
        if (!repositoryObservers.contains(repositoryObserver)) {
            repositoryObservers.add(repositoryObserver);
        }
    }

    public void notifyObserver() {
        for (RepositoryObserver repositoryObserver : repositoryObservers) {
            repositoryObserver.onDataChanged();
        }
    }

    public void removeContentObserver(RepositoryObserver repositoryObserver) {
        if (repositoryObservers.contains(repositoryObserver)) {
            repositoryObservers.remove(repositoryObserver);
        }
    }

    public interface RepositoryObserver {
        void onDataChanged();
    }
}

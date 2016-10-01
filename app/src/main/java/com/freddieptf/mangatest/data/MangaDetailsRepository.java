package com.freddieptf.mangatest.data;

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

    private MangaDetailsRepository(Context context) {
        localSource = new MangaDetailsLocalSource(context);
        remoteSource = new MangaDetailsRemoteSource(context);
    }

    public static MangaDetailsRepository getInstance(Context context) {
        if (INSTANCE == null) INSTANCE = new MangaDetailsRepository(context);
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
    public ArrayList<MangaDetails> getMangaDetailsList() {
        cache = localSource.getMangaDetailsList();
        return localSource.getMangaDetailsList();
    }

    @Override
    public MangaDetails getMangaDetails(@Nullable String id, @Nullable String name, @NonNull String source) {
        MangaDetails mangaDetails = localSource.getMangaDetails(id, name, source);
        if (mangaDetails == null) {
            mangaDetails = remoteSource.getMangaDetails(id, name, source);
        }
        return mangaDetails;
    }

    @Override
    public void saveMangaDetails(@NonNull MangaDetails mangaDetails, @NonNull String mangaId, @NonNull String source) {
        localSource.saveMangaDetails(mangaDetails, mangaId, source);
        refreshRepository();
    }

    public void updateMangaDetails(String name, ContentValues contentValues) {
        localSource.updateMangaDetails(name, contentValues);
        refreshRepository();
    }

    @Override
    public void deleteMangaDetails(@NonNull String mangaName) {
        localSource.deleteMangaDetails(mangaName);
        notifyObserver();
    }

    @Override
    public void deleteAllMangaDetails() {
        localSource.deleteAllMangaDetails();
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

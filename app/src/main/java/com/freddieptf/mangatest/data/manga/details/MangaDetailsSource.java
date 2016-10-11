package com.freddieptf.mangatest.data.manga.details;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.freddieptf.mangatest.data.model.MangaDetails;

import java.util.ArrayList;

/**
 * Created by freddieptf on 28/09/16.
 */

public interface MangaDetailsSource {

    ArrayList<MangaDetails> getMangaDetailsList();

    MangaDetails getMangaDetails(@Nullable String id, @Nullable String name, @NonNull String source);

    void saveMangaDetails(@NonNull MangaDetails mangaDetails, @NonNull String mangaId, @NonNull String source);

    void deleteMangaDetails(@NonNull String mangaName);

    void deleteAllMangaDetails();

    interface GetMangaDetailsCallback {
        void onDetailsLoaded(MangaDetails mangaDetails);

        void onDataNotAvailable();
    }
}

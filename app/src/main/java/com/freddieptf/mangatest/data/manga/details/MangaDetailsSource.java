package com.freddieptf.mangatest.data.manga.details;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.freddieptf.mangatest.data.model.MangaDetails;

import java.util.ArrayList;

/**
 * Created by freddieptf on 28/09/16.
 */

public interface MangaDetailsSource {

    ArrayList<MangaDetails> getMangaDetailsList(Context context);

    MangaDetails getMangaDetails(@Nullable String id, @Nullable String name, @NonNull String source, Context context);

    void saveMangaDetails(@NonNull MangaDetails mangaDetails, @NonNull String mangaId, @NonNull String source, Context context);

    void deleteMangaDetails(@NonNull String mangaName, @NonNull Context context);

    void deleteAllMangaDetails(@NonNull Context context);

    interface GetMangaDetailsCallback {
        void onDetailsLoaded(MangaDetails mangaDetails);

        void onDataNotAvailable();
    }
}

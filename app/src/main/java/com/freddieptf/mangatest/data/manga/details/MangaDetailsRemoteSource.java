package com.freddieptf.mangatest.data.manga.details;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.model.MangaDetails;
import com.freddieptf.mangatest.data.remote.MangaFox;
import com.freddieptf.mangatest.data.remote.MangaReader;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by freddieptf on 28/09/16.
 */

public class MangaDetailsRemoteSource implements MangaDetailsSource {


    public MangaDetailsRemoteSource() {
    }

    @Override
    public ArrayList<MangaDetails> getMangaDetailsList(Context context) {
        //no-op
        return null;
    }

    @Override
    public MangaDetails getMangaDetails(@Nullable String id, @Nullable String name, @NonNull String source, Context context) {
        MangaDetails mangaDetails;
        try {
            if (source.equals(context.getString(R.string.pref_manga_reader))) {
                mangaDetails = MangaReader.getInstance().getManga(id);
            } else {
                mangaDetails = MangaFox.getInstance().getManga(id);
            }
            return mangaDetails;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void saveMangaDetails(@NonNull MangaDetails mangaDetails, @NonNull String mangaId, @NonNull String source, Context context) {
        //no-op
    }

    @Override
    public void deleteMangaDetails(@NonNull String mangaName, Context context) {
        //no-op
    }

    @Override
    public void deleteAllMangaDetails(Context context) {
        //no-op
    }
}

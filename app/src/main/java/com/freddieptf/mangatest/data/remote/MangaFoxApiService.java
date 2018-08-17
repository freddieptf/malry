package com.freddieptf.mangatest.data.remote;

import com.freddieptf.mangatest.data.model.ChapterPages;
import com.freddieptf.mangatest.data.model.MangaDetails;
import com.freddieptf.mangatest.data.model.MangaItem;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by freddieptf on 08/09/16.
 */

interface MangaFoxApiService {

    @GET("/mangafox.me/")
    Call<ArrayList<MangaItem>> getMangaList();

    @GET("/mangafox.me/manga/{mangaId}/")
    Call<MangaDetails> getManga(@Path("mangaId") String mangaId);

    @GET("/mangafox.me/manga/{mangaId}/{chId}")
    Call<ChapterPages> getChapterPages(@Path("mangaId") String mangaId, @Path("chId") String chId);
}

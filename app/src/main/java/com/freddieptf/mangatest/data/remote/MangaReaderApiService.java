package com.freddieptf.mangatest.data.remote;

import com.freddieptf.mangatest.data.model.ChapterPages;
import com.freddieptf.mangatest.data.model.LatestMangaItem;
import com.freddieptf.mangatest.data.model.MangaDetails;
import com.freddieptf.mangatest.data.model.MangaItem;
import com.freddieptf.mangatest.data.model.PopularMangaItem;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by freddieptf on 08/09/16.
 */
interface MangaReaderApiService {

    @GET("/api/mr/list")
    Call<ArrayList<MangaItem>> getMangaList();

    @GET("/api/mr/latest")
    Call<ArrayList<LatestMangaItem>> getLatestMangaList();

    @GET("/api/mr/popular")
    Call<ArrayList<PopularMangaItem>> getPopularMangaList();

    @GET("/api/mr/manga/{mangaId}")
    Call<MangaDetails> getManga(@Path("mangaId") String mangaId);

    @GET("/api/mr/manga/{mangaId}/{chId}")
    Call<ChapterPages> getChapterPages(@Path("mangaId") String mangaId, @Path("chId") String chId);

}

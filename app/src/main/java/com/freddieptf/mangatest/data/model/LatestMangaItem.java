package com.freddieptf.mangatest.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fred on 7/26/15.
 */
public class LatestMangaItem {

    @SerializedName("release_date")
    private final String releaseDate;
    @SerializedName("manga")
    private final String mangaTitle;
    @SerializedName("chapter")
    private final String chapter;
    private final String mangaId;

    public LatestMangaItem(String mangaTitle, String mangaId, String chapter, String releaseDate) {
        this.mangaTitle = mangaTitle;
        this.mangaId = mangaId;
        this.chapter = chapter;
        this.releaseDate = releaseDate;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getMangaTitle() {
        return mangaTitle;
    }

    public String getMangaId() {
        return mangaId;
    }

    public String getChapter() {
        return chapter;
    }


}

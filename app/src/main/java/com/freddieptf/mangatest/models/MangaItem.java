package com.freddieptf.mangatest.models;

/**
 * Created by fred on 7/23/15.
 */
public class MangaItem {

    private final String mangaId;
    private final String name;

    public MangaItem(String mangaId, String name) {
        this.mangaId = mangaId;
        this.name = name;
    }

    public String getMangaId() {
        return mangaId;
    }

    public String getName() {
        return name;
    }
}

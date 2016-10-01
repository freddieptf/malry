package com.freddieptf.mangatest.data.model;

/**
 * Created by fred on 9/10/15.
 */
public class PopularMangaItem {

    private final String name;
    private final String chapterCount;
    private final String author;
    private final String genre;

    public PopularMangaItem(String name, String chapterCount, String author, String genre) {
        this.name = name;
        this.chapterCount = chapterCount;
        this.author = author;
        this.genre = genre;
    }

    public String getName() {
        return name;
    }

    public String getChapterCount() {
        return chapterCount;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

}

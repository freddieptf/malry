package com.freddieptf.mangatest.beans;

/**
 * Created by fred on 7/26/15.
 */
public class MangaLatestInfoBean {

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMangaTitle() {
        return mangaTitle;
    }

    public void setMangaTitle(String mangaTitle) {
        this.mangaTitle = mangaTitle;
    }

    public String getMangaId() {
        return mangaId;
    }

    public void setMangaId(String mangaId) {
        this.mangaId = mangaId;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = "" + chapter;
    }

    String date, mangaTitle, mangaId, chapter;

}

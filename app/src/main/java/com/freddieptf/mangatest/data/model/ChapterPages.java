package com.freddieptf.mangatest.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fred on 1/21/15.
 */
public class ChapterPages implements Parcelable {

    public static final Creator<ChapterPages> CREATOR = new Creator<ChapterPages>() {
        @Override
        public ChapterPages createFromParcel(Parcel in) {
            return new ChapterPages(in);
        }

        @Override
        public ChapterPages[] newArray(int size) {
            return new ChapterPages[size];
        }
    };
    private String mangaName;
    private String chapter;
    @SerializedName("name")
    private String chapterTitle;
    @SerializedName("pages")
    private ImagePage[] imagePages;

    public ChapterPages(String mangaName, String chapter, String chapterTitle, ImagePage[] pages) {
        this.mangaName = mangaName;
        this.chapter = chapter;
        this.chapterTitle = chapterTitle;
        this.imagePages = pages;
    }

    protected ChapterPages(Parcel in) {
        mangaName = in.readString();
        chapter = in.readString();
        chapterTitle = in.readString();
        imagePages = in.createTypedArray(ImagePage.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mangaName);
        dest.writeString(chapter);
        dest.writeString(chapterTitle);
        dest.writeTypedArray(imagePages, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public String getMangaName() {
        return mangaName;
    }

    public void setMangaName(String mangaName) {
        this.mangaName = mangaName;
    }

    public ImagePage[] getImagePages() {
        return imagePages;
    }

}

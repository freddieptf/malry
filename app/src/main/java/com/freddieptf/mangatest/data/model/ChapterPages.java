package com.freddieptf.mangatest.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fred on 1/21/15.
 */
public class ChapterPages implements Parcelable {

    public static final Creator<ChapterPages> CREATOR
            = new Creator<ChapterPages>() {
        public ChapterPages createFromParcel(Parcel in) {
            return new ChapterPages(in);
        }

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

    private ChapterPages(Parcel in) {
        readFromParcel(in);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mangaName);
        parcel.writeString(chapter);
        parcel.writeString(chapterTitle);
        parcel.writeParcelableArray(imagePages, 0);
    }

    private void readFromParcel(Parcel in) {
        mangaName = in.readString();
        chapter = in.readString();
        chapterTitle = in.readString();
        //// FIXME: 22/09/16 does this work?
        imagePages = (ImagePage[]) in.readParcelableArray(ImagePage.class.getClassLoader());
    }


}

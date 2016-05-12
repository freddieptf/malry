package com.freddieptf.mangatest.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fred on 1/21/15.
 */
public class NetworkChapterAttrs implements Parcelable {

    public static final Creator<NetworkChapterAttrs> CREATOR
            = new Creator<NetworkChapterAttrs>() {
        public NetworkChapterAttrs createFromParcel(Parcel in) {
            return new NetworkChapterAttrs(in);
        }

        public NetworkChapterAttrs[] newArray(int size) {
            return new NetworkChapterAttrs[size];
        }
    };
    private String pageId;
    private String imageUrl;
    private String name;
    private String chapter;
    private String chapterTitle;

    public NetworkChapterAttrs() {
        super();
    }

    public NetworkChapterAttrs(Parcel in) {
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

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(chapter);
        parcel.writeString(chapterTitle);
        parcel.writeString(pageId);
        parcel.writeString(imageUrl);

    }

    public void readFromParcel(Parcel in) {
        name = in.readString();
        chapter = in.readString();
        chapterTitle = in.readString();
        pageId = in.readString();
        imageUrl = in.readString();
    }


}

package com.freddieptf.mangatest.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by freddieptf on 22/09/16.
 */

public class ImagePage implements Parcelable {
    public static final Creator<ImagePage> CREATOR = new Creator<ImagePage>() {
        @Override
        public ImagePage createFromParcel(Parcel in) {
            return new ImagePage(in);
        }

        @Override
        public ImagePage[] newArray(int size) {
            return new ImagePage[size];
        }
    };
    private final String pageId;
    private final String url;

    public ImagePage(String pageId, String url) {
        this.pageId = pageId;
        this.url = url;
    }

    private ImagePage(Parcel in) {
        pageId = in.readString();
        url = in.readString();
    }

    public String getPageId() {
        return pageId;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pageId);
        dest.writeString(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}

package com.freddieptf.mangatest.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.freddieptf.mangatest.utils.Utilities;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by fred on 2/11/15.
 */
public class Chapter implements Parcelable {

    public static final Creator<Chapter> CREATOR = new Creator<Chapter>() {
        @Override
        public Chapter createFromParcel(Parcel in) {
            return new Chapter(in);
        }

        @Override
        public Chapter[] newArray(int size) {
            return new Chapter[size];
        }
    };
    private static final String TAG = "Chapter";
    public String chapterId;
    @SerializedName("name")
    public String chapterTitle;

    private Chapter() {
    }

    private Chapter(JSONObject object) {
        try {
            chapterId = object.getString("chapterId");
            chapterTitle = object.getString("name");
        } catch (JSONException e) {
            Utilities.Log(TAG, e.getMessage());
        }
    }

    private Chapter(Parcel in) {
        chapterId = in.readString();
        chapterTitle = in.readString();
    }

    public static Chapter[] fromJSON(JSONArray array) {
        Chapter[] chapters = new Chapter[array.length()];
        for (int i = array.length() - 1; i >= 0; i--) {
            try {
                Chapter c = new Chapter(array.getJSONObject(i));
                chapters[i] = c;
            } catch (JSONException e) {
                Utilities.Log("chapterAttrs", e.getMessage());
            }
        }
        return chapters;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chapterId);
        dest.writeString(chapterTitle);
    }

    @Override
    public int describeContents() {
        return 0;
    }


}

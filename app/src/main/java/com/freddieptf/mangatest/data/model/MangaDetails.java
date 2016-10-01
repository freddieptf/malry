package com.freddieptf.mangatest.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created by fred on 4/27/15.
 */
public class MangaDetails implements Parcelable {

    public static final Creator<MangaDetails> CREATOR = new Creator<MangaDetails>() {
        @Override
        public MangaDetails createFromParcel(Parcel parcel) {
            return new MangaDetails(parcel);
        }

        @Override
        public MangaDetails[] newArray(int i) {
            return new MangaDetails[i];
        }
    };
    private final String name;
    private final String id;
    private final String status;
    private final String info;
    private final String cover;
    @JsonAdapter(AuthorAdapter.class)
    private final String author;
    private final String lastUpdate;
    private final String source;
    @SerializedName("chapters")
    private Chapter[] chapters;

    private MangaDetails(Builder builder) {
        name = builder.name;
        id = builder.id;
        chapters = builder.chapters;
        author = builder.author;
        cover = builder.cover;
        info = builder.info;
        lastUpdate = builder.lastUpdate;
        source = builder.source;
        status = builder.status;
    }

    private MangaDetails(Parcel in) {
        name = in.readString();
        id = in.readString();
        status = in.readString();
        info = in.readString();
        cover = in.readString();
        author = in.readString();
        lastUpdate = in.readString();
        source = in.readString();
        chapters = (Chapter[]) in.readArray(Chapter.class.getClassLoader());
    }

    @Override
    public String toString() {
        return name + "\n" + author + "\n" + info + "\n" + chapters.length;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(id);
        parcel.writeString(status);
        parcel.writeString(info);
        parcel.writeString(cover);
        parcel.writeString(author);
        parcel.writeString(lastUpdate);
        parcel.writeString(source);
        parcel.writeParcelableArray(chapters, 0);
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getInfo() {
        return info;
    }

    public String getCover() {
        return cover;
    }

    public String getAuthor() {
        return author;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public String getSource() {
        return source;
    }

    public Chapter[] getChapters() {
        return chapters;
    }

    public static class Builder {
        private final String name;
        private final Chapter[] chapters;
        private String id;
        private String status;
        private String info;
        private String cover;
        private String author;
        private String lastUpdate;
        private String source;

        public Builder(String name, Chapter[] chapters) {
            this.name = name;
            this.chapters = chapters;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setInfo(String info) {
            this.info = info;
            return this;
        }

        public Builder setCover(String cover) {
            this.cover = cover;
            return this;
        }

        public Builder setAuthor(String author) {
            this.author = author;
            return this;
        }

        public Builder setLastUpdate(String lastUpdate) {
            this.lastUpdate = lastUpdate;
            return this;
        }

        public Builder setSource(String source) {
            this.source = source;
            return this;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public MangaDetails build() {
            return new MangaDetails(this);
        }

    }

    private static class AuthorAdapter extends TypeAdapter<String> {
        @Override
        public void write(JsonWriter out, String value) throws IOException {
            //no-op
        }

        @Override
        public String read(JsonReader in) throws IOException {
            JsonToken jsonToken = in.peek();
            if (jsonToken == JsonToken.BEGIN_ARRAY) {
                in.beginArray();
                String author = "";
                do {
                    author += in.nextString() + ", ";
                } while (in.hasNext());
                author = author.trim();
                author = author.substring(0, author.length() - 1);
                in.endArray();
                return author;
            } else if (jsonToken == JsonToken.STRING) {
                return in.nextString();
            }
            return null;
        }
    }


}

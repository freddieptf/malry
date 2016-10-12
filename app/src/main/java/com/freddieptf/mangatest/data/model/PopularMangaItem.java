package com.freddieptf.mangatest.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fred on 9/10/15.
 */
public class PopularMangaItem {

    private final String name;
    @SerializedName("id")
    private final String mangaId;
    private final String details;
    private final String author;
    private final int rank;
    private final String[] genre;

    private PopularMangaItem(Builder builder) {
        this.name = builder.name;
        this.mangaId = builder.mangaId;
        this.rank = builder.rank;
        this.details = builder.details;
        this.author = builder.author;
        this.genre = builder.genres;
    }

    public String getName() {
        return name;
    }

    public String getMangaId() {
        return mangaId;
    }

    public String getDetails() {
        return details;
    }

    public String getAuthor() {
        return author;
    }

    public String[] getGenre() {
        return genre;
    }

    public int getRank() {
        return rank;
    }

    public static class Builder {
        private final String name;
        private final String mangaId;
        private int rank;
        private String author;
        private String details;
        private String[] genres;

        public Builder(String name, String mangaId) {
            this.name = name;
            this.mangaId = mangaId;
        }

        public Builder setRank(int rank) {
            this.rank = rank;
            return this;
        }

        public Builder setAuthor(String author) {
            this.author = author;
            return this;
        }

        public Builder setDetails(String details) {
            this.details = details;
            return this;
        }

        public Builder setGenres(String[] genres) {
            this.genres = genres;
            return this;
        }

        public PopularMangaItem build() {
            return new PopularMangaItem(this);
        }
    }

}

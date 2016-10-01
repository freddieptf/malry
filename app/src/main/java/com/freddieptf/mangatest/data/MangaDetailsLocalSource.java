package com.freddieptf.mangatest.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.freddieptf.mangatest.data.local.Contract;
import com.freddieptf.mangatest.data.local.DbInsertHelper;
import com.freddieptf.mangatest.data.model.Chapter;
import com.freddieptf.mangatest.data.model.MangaDetails;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by freddieptf on 28/09/16.
 */

public class MangaDetailsLocalSource implements MangaDetailsSource {

    private static MangaDetailsLocalSource INSTANCE;
    private final String[] columns = {
            Contract.MyManga._ID,
            Contract.MyManga.COLUMN_MANGA_NAME,
            Contract.MyManga.COLUMN_MANGA_AUTHOR,
            Contract.MyManga.COLUMN_MANGA_STATUS,
            Contract.MyManga.COLUMN_MANGA_INFO,
            Contract.MyManga.COLUMN_MANGA_COVER,
            Contract.MyManga.COLUMN_MANGA_ID,
    };
    private final String[] full_columns = {
            Contract.MyManga._ID,
            Contract.MyManga.COLUMN_MANGA_NAME,
            Contract.MyManga.COLUMN_MANGA_AUTHOR,
            Contract.MyManga.COLUMN_MANGA_STATUS,
            Contract.MyManga.COLUMN_MANGA_INFO,
            Contract.MyManga.COLUMN_MANGA_COVER,
            Contract.MyManga.COLUMN_MANGA_ID,
            Contract.MyManga.COLUMN_MANGA_SOURCE,
            Contract.MyManga.COLUMN_MANGA_LAST_UPDATE,
            Contract.MyManga.COLUMN_MANGA_CHAPTER_JSON
    };
    private final int COLUMN_ID = 0;
    private final int COLUMN_MANGA_NAME = 1;
    private final int COLUMN_MANGA_AUTHOR = 2;
    private final int COLUMN_MANGA_STATUS = 3;
    private final int COLUMN_MANGA_INFO = 4;
    private final int COLUMN_MANGA_COVER = 5;
    private final int COLUMN_MANGA_ID = 6;
    private final int COLUMN_MANGA_SOURCE = 7;
    private final int COLUMN_MANGA_LAST_UPDATE = 8;
    private final int COLUMN_CHAPTER_JSON = 9;
    private ContentResolver contentResolver;

    public MangaDetailsLocalSource(Context context) {
        contentResolver = context.getContentResolver();
    }

    @Override
    public ArrayList<MangaDetails> getMangaDetailsList() {
        Cursor cursor = contentResolver.query(Contract.MyManga.CONTENT_URI, columns, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        } else {
            cursor.moveToFirst();
            ArrayList<MangaDetails> mangaDetailsList = new ArrayList<>(cursor.getCount());
            do {
                MangaDetails.Builder builder =
                        new MangaDetails.Builder(cursor.getString(COLUMN_MANGA_NAME), new Chapter[]{});

                builder.setAuthor(cursor.getString(COLUMN_MANGA_AUTHOR))
                        .setCover(cursor.getString(COLUMN_MANGA_COVER))
                        .setInfo(cursor.getString(COLUMN_MANGA_INFO))
                        .setStatus(cursor.getString(COLUMN_MANGA_STATUS));

                mangaDetailsList.add(builder.build());

            } while (cursor.moveToNext());
            if (!cursor.isClosed()) cursor.close();
            return mangaDetailsList;
        }
    }

    @Override
    public MangaDetails getMangaDetails(@Nullable String id, @Nullable String name, @NonNull String source) {

        Uri uri = Contract.MyManga.buildMangaWithNameUri(name);
        Cursor cursor = contentResolver.query(uri, full_columns, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        } else {
            cursor.moveToFirst();
            try {
                MangaDetails.Builder builder =
                        new MangaDetails.Builder(cursor.getString(COLUMN_MANGA_NAME),
                                Chapter.fromJSON(new JSONArray(cursor.getString(COLUMN_CHAPTER_JSON))));

                builder.setAuthor(cursor.getString(COLUMN_MANGA_AUTHOR))
                        .setCover(cursor.getString(COLUMN_MANGA_COVER))
                        .setInfo(cursor.getString(COLUMN_MANGA_INFO))
                        .setStatus(cursor.getString(COLUMN_MANGA_STATUS))
                        .setId(cursor.getString(COLUMN_MANGA_ID))
                        .setSource(cursor.getString(COLUMN_MANGA_SOURCE));

                if (!cursor.isClosed()) cursor.close();

                return builder.build();

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public void saveMangaDetails(@NonNull MangaDetails mangaDetails, @NonNull String mangaId, @NonNull String source) {
        DbInsertHelper.insertToLibrary(contentResolver, mangaDetails, mangaId, source);
    }

    public void updateMangaDetails(String mangaName, ContentValues contentValues) {
        Uri uri = Contract.MyManga.buildMangaWithNameUri(mangaName);
        contentResolver.update(uri, contentValues, null, null);
    }

    @Override
    public void deleteMangaDetails(@NonNull String mangaName) {
        Uri uri = Contract.MyManga.buildMangaWithNameUri(mangaName);
        contentResolver.delete(uri, null, null);
    }

    @Override
    public void deleteAllMangaDetails() {
        contentResolver.delete(Contract.MyManga.CONTENT_URI, null, null);
    }
}

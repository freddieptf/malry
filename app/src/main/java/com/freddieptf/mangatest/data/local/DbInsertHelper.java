package com.freddieptf.mangatest.data.local;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.freddieptf.mangatest.data.local.Contract.MangaReaderMangaList;
import com.freddieptf.mangatest.data.model.Chapter;
import com.freddieptf.mangatest.data.model.LatestMangaItem;
import com.freddieptf.mangatest.data.model.MangaDetails;
import com.freddieptf.mangatest.data.model.MangaItem;
import com.freddieptf.mangatest.data.model.PopularMangaItem;
import com.freddieptf.mangatest.utils.Utilities;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by fred on 7/30/15.
 */
public class DbInsertHelper {

    public static final String LOG_TAG = "DbInsertHelper";
    private Uri destination;

    public DbInsertHelper() {
    }

    public static void insertToLibrary(ContentResolver contentResolver, MangaDetails mangaDetails, String mangaId, String source) {
        Uri uri = Contract.MyManga.buildMangaWithNameUri(mangaDetails.getName());
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.MyManga.COLUMN_MANGA_NAME, mangaDetails.getName());
        contentValues.put(Contract.MyManga.COLUMN_MANGA_ID, mangaId);
        contentValues.put(Contract.MyManga.COLUMN_MANGA_AUTHOR, mangaDetails.getAuthor());
        contentValues.put(Contract.MyManga.COLUMN_MANGA_INFO, mangaDetails.getInfo());
        contentValues.put(Contract.MyManga.COLUMN_MANGA_COVER, mangaDetails.getCover());
        contentValues.put(Contract.MyManga.COLUMN_MANGA_STATUS, mangaDetails.getStatus());
        contentValues.put(Contract.MyManga.COLUMN_MANGA_CHAPTER_JSON, new Gson().toJson(mangaDetails.getChapters()));
        contentValues.put(Contract.MyManga.COLUMN_MANGA_SOURCE, source);
        contentValues.put(Contract.MyManga.COLUMN_MANGA_LAST_UPDATE, mangaDetails.getLastUpdate());

        Chapter[] array = mangaDetails.getChapters();
        contentValues.put(Contract.MyManga.COLUMN_MANGA_LATEST_CHAPTER, array[0].chapterId);
        Uri insertUri = contentResolver.insert(Contract.MyManga.CONTENT_URI, contentValues);
        Log.d(DbInsertHelper.LOG_TAG, insertUri.toString());

    }

    public DbInsertHelper setDestinationUri(Uri destination) {
        this.destination = destination;
        return this;
    }

    public Callable<Boolean> insertMangaList(final Context context, final ArrayList<MangaItem> list) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                List<ContentValues> contentValuesList = new ArrayList<>(list.size());

                for (MangaItem m : list) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MangaReaderMangaList.COLUMN_MANGA_NAME, m.getName());
                    contentValues.put(MangaReaderMangaList.COLUMN_MANGA_ID, m.getMangaId());
                    contentValuesList.add(contentValues);
                }

                int rowsInserted = -1;
                if (contentValuesList.size() > 0) {
                    ContentValues[] contentValuesArray = new ContentValues[contentValuesList.size()];
                    contentValuesList.toArray(contentValuesArray);
                    contentValuesList.clear();
                    rowsInserted = context.getContentResolver().bulkInsert(destination, contentValuesArray);
                    //// FIXME: 12/09/16 search table needs some rethinking
                    int virtualRowsInserted = context.getContentResolver().bulkInsert(Contract.VirtualTable.CONTENT_URI, contentValuesArray);
                }
                Utilities.Log(LOG_TAG, "Rows inserted: " + rowsInserted);
                return rowsInserted > 0;
            }
        };
    }

    public Callable<Boolean> insertLatestList(final Context context, final ArrayList<LatestMangaItem> list) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                List<ContentValues> contentValuesList = new ArrayList<>(list.size());
                for (LatestMangaItem m : list) {
                    ContentValues cv = new ContentValues();
                    cv.put(Contract.MangaReaderLatestList.COLUMN_MANGA_NAME, m.getMangaTitle());
                    cv.put(Contract.MangaReaderLatestList.COLUMN_MANGA_ID, m.getMangaId());
                    cv.put(Contract.MangaReaderLatestList.COLUMN_CHAPTER, m.getChapter());
                    cv.put(Contract.MangaReaderLatestList.COLUMN_DATE, m.getReleaseDate());
                    contentValuesList.add(cv);
                }

                int rowsInserted = -1;
                if (contentValuesList.size() > 0) {
                    ContentValues[] contentValues = new ContentValues[contentValuesList.size()];
                    contentValuesList.toArray(contentValues);
                    contentValuesList.clear();
                    rowsInserted = context.getContentResolver().bulkInsert(destination, contentValues);
                }
                Utilities.Log(LOG_TAG, "Rows inserted: " + rowsInserted);
                return rowsInserted > 0;
            }
        };
    }

    public Callable<Boolean> insertPopularList(final Context context, final ArrayList<PopularMangaItem> list) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                List<ContentValues> contentValuesList = new ArrayList<>(list.size());
                for (PopularMangaItem m : list) {
                    ContentValues cv = new ContentValues();
                    cv.put(Contract.MangaReaderPopularList.COLUMN_MANGA_NAME, m.getName());
                    cv.put(Contract.MangaReaderPopularList.COLUMN_CHAPTER_DETAILS, m.getDetails());
                    cv.put(Contract.MangaReaderPopularList.COLUMN_MANGA_AUTHOR, m.getAuthor());
                    cv.put(Contract.MangaReaderPopularList.COLUMN_MANGA_GENRE, Arrays.toString(m.getGenre()));
                    cv.put(Contract.MangaReaderPopularList.COLUMN_MANGA_ID, m.getMangaId());
                    cv.put(Contract.MangaReaderPopularList.COLUMN_MANGA_RANK, m.getRank());
                    contentValuesList.add(cv);
                }

                int rowsInserted = -1;
                if (contentValuesList.size() > 0) {
                    ContentValues[] contentValues = new ContentValues[contentValuesList.size()];
                    contentValuesList.toArray(contentValues);
                    contentValuesList.clear();
                    rowsInserted = context.getContentResolver().bulkInsert(destination, contentValues);
                    Utilities.Log(LOG_TAG, "Rows inserted: " + rowsInserted);
                }
                return rowsInserted > 0;
            }
        };
    }
}

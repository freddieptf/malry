package com.freddieptf.mangatest.data.manga.lists;

import android.content.Context;
import android.database.Cursor;

import com.freddieptf.mangatest.data.local.Contract;
import com.freddieptf.mangatest.data.model.LatestMangaItem;
import com.freddieptf.mangatest.data.model.MangaItem;
import com.freddieptf.mangatest.data.model.PopularMangaItem;

import java.util.ArrayList;

/**
 * Created by freddieptf on 11/10/16.
 */
public class MangaListLocalSource implements MangaListSource {

    private static MangaListLocalSource INSTANCE;
    private final String[] MANGA_COLUMNS = {
            Contract.MangaReaderMangaList._ID,
            Contract.MangaReaderMangaList.COLUMN_MANGA_NAME,
            Contract.MangaReaderMangaList.COLUMN_MANGA_ID
    };
    private final int MANGA_ID = 2;
    private final int MANGA_NAME = 1;

    private MangaListLocalSource() {
    }

    public static MangaListLocalSource getInstance() {
        if (INSTANCE == null) INSTANCE = new MangaListLocalSource();
        return INSTANCE;
    }

    @Override
    public ArrayList<MangaItem> getMangaReaderMangaList(Context context) {
        Cursor cursor = context.getContentResolver().query(Contract.MangaReaderMangaList.CONTENT_URI,
                MANGA_COLUMNS, null, null, null);

        if (cursor == null || !cursor.moveToFirst()) return null;

        cursor.moveToFirst();
        ArrayList<MangaItem> mangaItems = new ArrayList<>(cursor.getCount());
        do {
            MangaItem mangaItem = new MangaItem(cursor.getString(MANGA_ID), cursor.getString(MANGA_NAME));
            mangaItems.add(mangaItem);
        } while (cursor.moveToNext());

        if (!cursor.isClosed()) cursor.close();

        return mangaItems;
    }

    @Override
    public ArrayList<MangaItem> getMangaFoxMangaList(Context context) {
        Cursor cursor = context.getContentResolver().query(Contract.MangaFoxMangaList.CONTENT_URI,
                MANGA_COLUMNS, null, null, null);

        if (cursor == null || !cursor.moveToFirst()) return null;

        cursor.moveToFirst();
        ArrayList<MangaItem> mangaItems = new ArrayList<>(cursor.getCount());
        do {
            MangaItem mangaItem = new MangaItem(cursor.getString(MANGA_ID), cursor.getString(MANGA_NAME));
            mangaItems.add(mangaItem);
        } while (cursor.moveToNext());

        if (!cursor.isClosed()) cursor.close();

        return mangaItems;
    }

    @Override
    public ArrayList<LatestMangaItem> getMangaReaderLatestList(Context context) {
        String[] LATEST_COLUMNS = {
                Contract.MangaReaderLatestList._ID,
                Contract.MangaReaderLatestList.COLUMN_MANGA_NAME,
                Contract.MangaReaderLatestList.COLUMN_MANGA_ID,
                Contract.MangaReaderLatestList.COLUMN_CHAPTER,
                Contract.MangaReaderLatestList.COLUMN_DATE
        };

        Cursor cursor = context.getContentResolver().query(Contract.MangaReaderLatestList.CONTENT_URI,
                LATEST_COLUMNS, null, null, null);

        if (cursor == null || !cursor.moveToFirst()) return null;

        cursor.moveToFirst();
        ArrayList<LatestMangaItem> latestMangaItems = new ArrayList<>(cursor.getCount());

        do {
            LatestMangaItem latestMangaItem =
                    new LatestMangaItem(cursor.getString(1), cursor.getString(2),
                            cursor.getString(3), cursor.getString(4));
            latestMangaItems.add(latestMangaItem);
        } while (cursor.moveToNext());

        if (!cursor.isClosed()) cursor.close();

        return latestMangaItems;
    }

    @Override
    public ArrayList<PopularMangaItem> getMangaReaderPopularList(Context context) {
        String[] POPULAR_COLUMNS = {
                Contract.MangaReaderPopularList._ID,
                Contract.MangaReaderPopularList.COLUMN_MANGA_NAME,
                Contract.MangaReaderPopularList.COLUMN_MANGA_AUTHOR,
                Contract.MangaReaderPopularList.COLUMN_CHAPTER_DETAILS,
                Contract.MangaReaderPopularList.COLUMN_MANGA_GENRE
        };
        return null;
    }
}

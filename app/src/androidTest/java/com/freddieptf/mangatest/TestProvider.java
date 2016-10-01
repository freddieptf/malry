package com.freddieptf.mangatest;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.freddieptf.mangatest.data.local.Contract;

import static com.freddieptf.mangatest.data.local.Contract.MangaReaderMangaList;
import static com.freddieptf.mangatest.data.local.Contract.MyManga;

/**
 * Created by fred on 1/30/15.
 */
public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void deleteAllRecords(){
        mContext.getContentResolver().delete(MyManga.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(MangaReaderMangaList.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(Contract.VirtualTable.CONTENT_URI, null, null);

        Cursor c = mContext.getContentResolver().query(MyManga.CONTENT_URI, null, null, null, null);
        assertEquals(0, c.getCount());
        c.close();

        Cursor c1 = mContext.getContentResolver().query(Contract.MangaReaderMangaList.CONTENT_URI, null, null, null, null);
        assertEquals(0, c1.getCount());
        c1.close();

        Cursor c3 = mContext.getContentResolver().query(Contract.VirtualTable.CONTENT_URI, null, null, null, null);
        assertEquals(0, c3.getCount());
        c3.close();


    }

    public void setUp(){
        deleteAllRecords();
    }


    public void testInsertReadProvider(){
        ContentValues contentValues = TestDB.mockMangaValues();
        Uri mangaUri = mContext.getContentResolver().insert(Contract.MyManga.CONTENT_URI, contentValues);
        long row_id = ContentUris.parseId(mangaUri);
        assertTrue(row_id != -1);

        Cursor cursor = mContext.getContentResolver().query(MyManga.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null,
                null,
                null);
        TestDB.validateCursor(cursor, contentValues);

        Cursor mangaCursor = mContext.getContentResolver().query(
                MyManga.buildMangaWithNameUri(TestDB.TEST_MANGA_NAME),
                null,
                null,
                null,
                null);

        TestDB.validateCursor(mangaCursor, contentValues);


        ContentValues contentValues1 = TestDB.mockMangaInListValues();
        Uri mangaInListUri = mContext.getContentResolver().insert(Contract.MangaReaderMangaList.CONTENT_URI, contentValues1);
        long row_id1 = ContentUris.parseId(mangaInListUri);
        assertTrue(row_id1 != -1);

        Cursor cursor1 = mContext.getContentResolver().query(MangaReaderMangaList.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null,
                null,
                null);
        TestDB.validateCursor(cursor1, contentValues1);

        Cursor mangaInListCursor = mContext.getContentResolver().query(
                MangaReaderMangaList.buildMangaInListWithNameUri(TestDB.TEST_MANGA_NAME),
                null,
                null,
                null,
                null);

        TestDB.validateCursor(mangaInListCursor, contentValues1);

        Uri mangaInVirtualList = mContext.getContentResolver().insert(Contract.VirtualTable.CONTENT_URI, contentValues1);
        long row_id2 = ContentUris.parseId(mangaInVirtualList);
        assertTrue(row_id2 != -1);

        Cursor cursor2 = mContext.getContentResolver().query(Contract.VirtualTable.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null,
                null,
                null);
        TestDB.validateCursor(cursor2, contentValues1);


    }

}

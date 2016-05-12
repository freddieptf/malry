package com.freddieptf.mangatest;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.freddieptf.mangatest.data.Contract;
import com.freddieptf.mangatest.data.DbHelper;

import java.util.Map;
import java.util.Set;

import static com.freddieptf.mangatest.data.Contract.MangaReaderMangaList;
import static com.freddieptf.mangatest.data.Contract.MyManga;

/**
 * Created by fred on 1/30/15.
 */
public class TestDB extends AndroidTestCase {

    public static final String LOG_TAG = TestDB.class.getSimpleName();
    public static String TEST_MANGA_NAME = "manga";

    static ContentValues mockMangaValues() {
        ContentValues cv = new ContentValues();

        cv.put(MyManga.COLUMN_MANGA_NAME, TEST_MANGA_NAME);
        cv.put(MyManga.COLUMN_MANGA_AUTHOR, "Fred");
        cv.put(MyManga.COLUMN_MANGA_INFO, "Blah blah blah");
        cv.put(MyManga.COLUMN_MANGA_STATUS, "On-going");
        cv.put(Contract.MyManga.COLUMN_MANGA_CHAPTER_JSON, "ycbuidvbuisvbuvbsdbvusvbs");
        cv.put(MyManga.COLUMN_MANGA_COVER, "http://google.com");

        return cv;
    }

    static ContentValues mockMangaInListValues() {
        ContentValues cv = new ContentValues();

        cv.put(Contract.MangaReaderMangaList.COLUMN_MANGA_ID, "-manga-");
        cv.put(Contract.MangaReaderMangaList.COLUMN_MANGA_NAME, TEST_MANGA_NAME);
        return cv;
    }

    static void validateCursor(Cursor c, ContentValues cv) {

        assertTrue(c.moveToFirst());

        Set<Map.Entry<String, Object>> values = cv.valueSet();
        for (Map.Entry<String, Object> entry : values) {
            String columnName = entry.getKey();
            int i = c.getColumnIndex(columnName);
            assertTrue(i != -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, c.getString(i));
        }


    }

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(DbHelper.DATABASE_NAME);
        SQLiteDatabase db = new DbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertToDb() throws Throwable {
        DbHelper myDbhelper = new DbHelper(mContext);
        SQLiteDatabase db = myDbhelper.getWritableDatabase();

        ContentValues contentValues = mockMangaValues();
        long manga_Id = db.insert(MyManga.TABLE_NAME, null, contentValues);

        assertTrue(manga_Id != -1);
        Log.d(LOG_TAG, "manga_Id: " + manga_Id);

        Cursor cursor = db.query(MyManga.TABLE_NAME, null, null, null, null, null, null);

        validateCursor(cursor, contentValues);

        ContentValues contentValues1 = mockMangaInListValues();
        long mangaInList_Id = db.insert(MangaReaderMangaList.TABLE_NAME, null, contentValues1);

        assertTrue(mangaInList_Id != -1);
        Log.d(LOG_TAG, "mangaInList_ID: " + mangaInList_Id);

        Cursor cursor1 = db.query(MangaReaderMangaList.TABLE_NAME, null, null, null, null, null, null);

        validateCursor(cursor1, contentValues1);



//        ContentValues contentValues2 = mockMangaValues2();
//        long manga_Id2 = db.insert(Contract.Manga.TABLE_NAME, null, contentValues2);
//
//        assertTrue(manga_Id2 != -1);
//        Log.d(LOG_TAG, "manga_Id: " + manga_Id2);
//
//        Cursor cursor2 = db.query(Contract.Manga.TABLE_NAME, null, null, null, null, null, null);
//
//        validateCursor(cursor2, contentValues2);



        db.close();
        myDbhelper.close();


    }


}

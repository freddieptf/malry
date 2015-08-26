package com.freddieptf.mangatest.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.freddieptf.mangatest.data.Contract.MangaFoxMangaList;
import static com.freddieptf.mangatest.data.Contract.MangaReaderMangaList;
import static com.freddieptf.mangatest.data.Contract.MyManga;

/**
 * Created by fred on 1/29/15.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "manga.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_MANGAREADER_LIST = "CREATE TABLE " + MangaReaderMangaList.TABLE_NAME + " (" +
                MangaReaderMangaList._ID + " INTEGER PRIMARY KEY, " +
                MangaReaderMangaList.COLUMN_MANGA_NAME + " TEXT NOT NULL, " +
                MangaReaderMangaList.COLUMN_MANGA_ID + " TEXT NOT NULL );";

        final String CREATE_MANGAREADER_LATEST_LIST = "CREATE TABLE " + Contract.MangaReaderLatestList.TABLE_NAME
                + " (" + Contract.MangaReaderLatestList._ID + " INTEGER PRIMARY KEY, "
                + Contract.MangaReaderLatestList.COLUMN_MANGA_NAME + " TEXT NOT NULL, "
                + Contract.MangaReaderLatestList.COLUMN_MANGA_ID + " TEXT NOT NULL, "
                + Contract.MangaReaderLatestList.COLUMN_CHAPTER + " TEXT NOT NULL, "
                + Contract.MangaReaderLatestList.COLUMN_DATE + " TEXT NOT NULL );";

        final String CREATE_VIRTUAL_TABLE = "CREATE VIRTUAL TABLE "
                + Contract.VirtualTable.TABLE_NAME + " USING fts3 ("
                + Contract.VirtualTable._ID + " INTEGER PRIMARY KEY, " +
                Contract.VirtualTable.COLUMN_MANGA_NAME + ", " +
                Contract.VirtualTable.COLUMN_MANGA_ID + ");";

        final String CREATE_MANGAFOX_LIST = "CREATE TABLE " + MangaFoxMangaList.TABLE_NAME + " (" +
                MangaFoxMangaList._ID + " INTEGER PRIMARY KEY, " +
                MangaFoxMangaList.COLUMN_MANGA_NAME + " TEXT NOT NULL, " +
                MangaFoxMangaList.COLUMN_MANGA_ID + " TEXT NOT NULL );";

        final String CREATE_MY_MANGA = "CREATE TABLE " + MyManga.TABLE_NAME + " (" +
                MyManga._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MyManga.COLUMN_MANGA_NAME + " TEXT NOT NULL, " +
                MyManga.COLUMN_MANGA_ID + " TEXT NOT NULL, " +
                MyManga.COLUMN_MANGA_AUTHOR + " TEXT NOT NULL, " +
                MyManga.COLUMN_MANGA_INFO + " TEXT NOT NULL, " +
                MyManga.COLUMN_MANGA_COVER + " TEXT NOT NULL, " +
                MyManga.COLUMN_MANGA_STATUS + " TEXT NOT NULL, " +
                MyManga.COLUMN_MANGA_LAST_UPDATE + " TEXT NOT NULL, " +
                MyManga.COLUMN_MANGA_LATEST_CHAPTER + " TEXT NOT NULL, " +
                MyManga.COLUMN_MANGA_SOURCE + " TEXT NOT NULL, " +
                MyManga.COLUMN_MANGA_CHAPTER_JSON + " TEXT NOT NULL );";

        final String CREATE_MANGAEDEN_LIST = "CREATE TABLE " + Contract.MangaEden.TABLE_NAME + " (" +
                Contract.MangaEden._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Contract.MangaEden.COLUMN_MANGA_TITLE + " TEXT NOT NULL, " +
                Contract.MangaEden.COLUMN_MANGA_ID + " TEXT NOT NULL, " +
                Contract.MangaEden.COLUMN_MANGA_HITS + " INTEGER NOT NULL, " +
                Contract.MangaEden.COLUMN_MANGA_STATUS + " TEXT NOT NULL, " +
                Contract.MangaEden.COLUMN_MANGA_COVER + " TEXT NOT NULL );";

        sqLiteDatabase.execSQL(CREATE_MANGAREADER_LIST);
        sqLiteDatabase.execSQL(CREATE_MANGAREADER_LATEST_LIST);
        sqLiteDatabase.execSQL(CREATE_MANGAFOX_LIST);
        sqLiteDatabase.execSQL(CREATE_MY_MANGA);
        sqLiteDatabase.execSQL(CREATE_MANGAEDEN_LIST);
        sqLiteDatabase.execSQL(CREATE_VIRTUAL_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MyManga.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MangaReaderMangaList.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MangaFoxMangaList.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Contract.MangaEden.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE ID EXISTS " + Contract.MangaReaderLatestList.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

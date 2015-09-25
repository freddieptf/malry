package com.freddieptf.mangatest.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.freddieptf.mangatest.data.Contract.MangaReaderLatestList;
import com.freddieptf.mangatest.data.Contract.MangaReaderPopularList;
import com.freddieptf.mangatest.data.Contract.VirtualTable;

import static com.freddieptf.mangatest.data.Contract.MangaEden;
import static com.freddieptf.mangatest.data.Contract.MangaFoxMangaList;
import static com.freddieptf.mangatest.data.Contract.MangaReaderMangaList;
import static com.freddieptf.mangatest.data.Contract.MyManga;

/**
 * Created by fred on 1/29/15.
 */

public class MangaProvider extends ContentProvider {

    private DbHelper myDbHelper;
    private static final UriMatcher mUriMatcher = myUriMatcher();
    private static final int MY_MANGA = 100;
    private static final int MY_MANGA_WITH_NAME = 101;
    private static final int MANGAREADER_LIST = 300;
    private static final int MANGA_IN_READER_LIST_WITH_NAME = 301;
    private static final int MANGAREADER_LATEST_LIST = 302;
    private static final int MANGAREADER_POPULAR_LIST = 303;
    private static final int MANGAREADER_POPULAR_LIST_WITH_GENRE = 304;
    private static final int MANGA_EDEN = 500;
    private static final int MANGA_EDEN_WITH_NAME = 501;
    private static final int MANGAFOX_LIST = 700;
    private static final int MANGA_IN_FOX_LIST_WITH_NAME = 701;
    private static final int VIRTUAL_TABLE = 900;
    private static final int VIRTUAL_TABLE_WITH_QUERY = 901;


    private static UriMatcher myUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String AUTH = Contract.CONTENT_AUTH;

        matcher.addURI(AUTH, Contract.PATH_MY_MANGA, MY_MANGA);
        matcher.addURI(AUTH, Contract.PATH_MY_MANGA + "/*", MY_MANGA_WITH_NAME);

        matcher.addURI(AUTH, Contract.PATH_MANGAREADER_LIST, MANGAREADER_LIST);
        matcher.addURI(AUTH, Contract.PATH_MANGAREADER_LIST + "/*", MANGA_IN_READER_LIST_WITH_NAME);

        matcher.addURI(AUTH, Contract.PATH_MANGAREADER_LATEST_LIST, MANGAREADER_LATEST_LIST);

        matcher.addURI(AUTH, Contract.PATH_MANGAREADER_POPULAR_LIST, MANGAREADER_POPULAR_LIST);
        matcher.addURI(AUTH, Contract.PATH_MANGAREADER_POPULAR_LIST + "/*", MANGAREADER_POPULAR_LIST_WITH_GENRE);

        matcher.addURI(AUTH, Contract.PATH_MANGAFOX_LIST, MANGAFOX_LIST);
        matcher.addURI(AUTH, Contract.PATH_MANGAFOX_LIST + "/*", MANGA_IN_FOX_LIST_WITH_NAME);

        matcher.addURI(AUTH, Contract.PATH_MANGA_EDEN, MANGA_EDEN);
        matcher.addURI(AUTH, Contract.PATH_MANGA_EDEN + "/*", MANGA_EDEN_WITH_NAME);

        matcher.addURI(AUTH, Contract.PATH_VIRTUAL_TABLE, VIRTUAL_TABLE);
        matcher.addURI(AUTH, Contract.PATH_VIRTUAL_TABLE + "/*",  VIRTUAL_TABLE_WITH_QUERY);

        return matcher;
    }

    private Cursor getMangabyName(Uri uri, String[] projection, String sortOrder){
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        String mangaName = MyManga.getMangaNameFromUri(uri);
        String[] selectionArgs;
        String selection;

        selection = MyManga.TABLE_NAME +
                    "." + MyManga.COLUMN_MANGA_NAME + " = ? ";
        selectionArgs = new String[]{mangaName};

        return db.query(MyManga.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

    }

    private int deleteMangaByName(Uri uri){
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        String mangaName = MyManga.getMangaNameFromUri(uri);
        String where = MyManga.TABLE_NAME + "." + MyManga.COLUMN_MANGA_NAME + " = ?";
        String[] whereArgs = new String[]{mangaName};

        return db.delete(MyManga.TABLE_NAME,
                where,
                whereArgs);

    }

    private int updateMangaByName(Uri uri, ContentValues contentValues){
        SQLiteDatabase db  = myDbHelper.getReadableDatabase();
        String mangaName = MyManga.getMangaNameFromUri(uri);
        String where = MyManga.TABLE_NAME + "." + MyManga.COLUMN_MANGA_NAME + " = ? ";
        String[] whereArgs = new String[]{mangaName};

        return db.update(MyManga.TABLE_NAME, contentValues, where, whereArgs);

    }

    private Cursor getMangaInVirtualTablebyQuery(Uri uri, String[] projection, String sortOrder){
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        String query = VirtualTable.getQueryFromVirtualMangaUri(uri);

        String selection = VirtualTable.TABLE_NAME + "." + VirtualTable.COLUMN_MANGA_NAME + " MATCH ?";
        String[] selectionArgs = new String[] {query + "*"};

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(VirtualTable.TABLE_NAME);

        return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    private Cursor getMangaInReaderListByGenre(Uri uri, String[] projection, String sortOrder){
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        String genre_query = MangaReaderPopularList.getGenreFromUri(uri);

        String selection = MangaReaderPopularList.TABLE_NAME + "." +
                MangaReaderPopularList.COLUMN_MANGA_GENRE + " MATCH ?";
        String[] selectionArgs = new String[]{genre_query + "*"};
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(MangaReaderPopularList.TABLE_NAME);
        return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    private Cursor getMangaInReaderListbyName(Uri uri, String[] projection, String sortOrder){
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        String mangaName = MangaReaderMangaList.getMangaNameInListFromUri(uri);
        String[] selectionArgs;
        String selection;

        selection = MangaReaderMangaList.TABLE_NAME +
                "." + MangaReaderMangaList.COLUMN_MANGA_NAME + " = ? ";
        selectionArgs = new String[]{mangaName};

        return db.query(MangaReaderMangaList.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMangaInFoxListbyName(Uri uri, String[] projection, String sortOrder){
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        String mangaName = MangaFoxMangaList.getMangaNameInListFromUri(uri);
        String[] selectionArgs;
        String selection;

        selection = MangaFoxMangaList.TABLE_NAME +
                "." + MangaFoxMangaList.COLUMN_MANGA_NAME + " = ? ";
        selectionArgs = new String[]{mangaName};

        return db.query(MangaFoxMangaList.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMangaInEdenListbyName(Uri uri, String[] projection, String sortOrder){
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        String mangaName = MangaEden.getMangaNameFromUri(uri);
        String[] selectionArgs;
        String selection;

        selection = MangaEden.TABLE_NAME +
                "." + MangaEden.COLUMN_MANGA_TITLE + " = ? ";
        selectionArgs = new String[]{mangaName};

        return db.query(MangaEden.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }


    @Override
    public boolean onCreate() {
        myDbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor returnCursor;

        switch (mUriMatcher.match(uri)) {
            case MY_MANGA: {
                returnCursor = myDbHelper.getReadableDatabase().query(MyManga.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            }
            case MY_MANGA_WITH_NAME: {
                returnCursor = getMangabyName(uri, projection, sortOrder);
                break;
            }

            case MANGAREADER_LIST: {
                returnCursor = myDbHelper.getReadableDatabase().query(MangaReaderMangaList.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            }
            case MANGA_IN_READER_LIST_WITH_NAME: {
                returnCursor = getMangaInReaderListbyName(uri, projection, sortOrder);
                break;
            }

            case MANGAREADER_LATEST_LIST:{
                returnCursor = myDbHelper.getReadableDatabase().query(MangaReaderLatestList.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            }

            case MANGAREADER_POPULAR_LIST:{
                returnCursor = myDbHelper.getReadableDatabase().query(MangaReaderPopularList.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            }

            case MANGAREADER_POPULAR_LIST_WITH_GENRE:{
                returnCursor = getMangaInReaderListByGenre(uri, projection, sortOrder);
                break;
            }

            case MANGAFOX_LIST: {
                returnCursor = myDbHelper.getReadableDatabase().query(MangaFoxMangaList.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            }
            case MANGA_IN_FOX_LIST_WITH_NAME: {
                returnCursor = getMangaInFoxListbyName(uri, projection, sortOrder);
                break;
            }

            case MANGA_EDEN: {
                returnCursor = myDbHelper.getReadableDatabase().query(MangaEden.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;

            }
            case MANGA_EDEN_WITH_NAME: {
                returnCursor = getMangaInEdenListbyName(uri, projection, sortOrder);
                break;
            }

            case VIRTUAL_TABLE: {
                returnCursor = myDbHelper.getReadableDatabase().query(VirtualTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs, null, null, sortOrder);
                break;
            }
            case VIRTUAL_TABLE_WITH_QUERY: {
                returnCursor = getMangaInVirtualTablebyQuery(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = myDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case MY_MANGA: {
                long id = db.insert(MyManga.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = MyManga.buildMangaUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert: " + uri);
                }
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int inserted = -1;
        final SQLiteDatabase db = myDbHelper.getWritableDatabase();
        final int match = myUriMatcher().match(uri);

        switch (match){
            case MANGAREADER_LIST:
                db.beginTransaction();
                for(ContentValues contentValues : values){
                    long id = db.insertOrThrow(MangaReaderMangaList.TABLE_NAME, null, contentValues);
                    if(id == -1) throw new android.database.SQLException("Failed to insert a row into " + MangaReaderMangaList.CONTENT_URI);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                inserted = values.length;
                break;

            case MANGAFOX_LIST:
                db.beginTransaction();
                for(ContentValues contentValues : values){
                    long id = db.insertOrThrow(MangaFoxMangaList.TABLE_NAME, null, contentValues);
                    if(id == -1) throw new android.database.SQLException("Failed to insert a row into " + MangaFoxMangaList.CONTENT_URI);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                inserted = values.length;
                break;

            case VIRTUAL_TABLE:
                db.beginTransaction();
                for(ContentValues contentValues : values){
                    long id = db.insertOrThrow(VirtualTable.TABLE_NAME, null, contentValues);
                    if(id == -1) throw new android.database.SQLException("Failed to insert a row into " + VirtualTable.CONTENT_URI);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                inserted = values.length;
                break;

            case MANGAREADER_LATEST_LIST:
                db.beginTransaction();
                for(ContentValues contentValues : values){
                    long id = db.insertOrThrow(MangaReaderLatestList.TABLE_NAME, null, contentValues);
                    if(id == -1) throw new android.database.SQLException("Failed to insert a row into " + MangaReaderLatestList.CONTENT_URI);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                inserted = values.length;
                break;

            case MANGAREADER_POPULAR_LIST:
                db.beginTransaction();
                for(ContentValues contentValues : values){
                    long id = db.insertOrThrow(MangaReaderPopularList.TABLE_NAME, null, contentValues);
                    if(id == -1) throw new android.database.SQLException("Failed to insert a row into " + MangaReaderPopularList.CONTENT_URI);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                inserted = values.length;
                break;

        }

        //notify so the cursor loaders can restart with the new data
        getContext().getContentResolver().notifyChange(uri, null);
        return inserted;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        final SQLiteDatabase db = myDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int rowsDeleted;

        switch (match){
            case MY_MANGA: {
                rowsDeleted = db.delete(MyManga.TABLE_NAME, s, strings);
                break;
            }
            case MY_MANGA_WITH_NAME: {
                rowsDeleted = deleteMangaByName(uri);
                break;
            }
            case MANGAREADER_LIST: {
                rowsDeleted = db.delete(MangaReaderMangaList.TABLE_NAME, s, strings);
                break;
            }
            case MANGAREADER_LATEST_LIST: {
                rowsDeleted = db.delete(MangaReaderLatestList.TABLE_NAME, s, strings);
                break;
            }
            case MANGAREADER_POPULAR_LIST: {
                rowsDeleted = db.delete(MangaReaderPopularList.TABLE_NAME, s, strings);
                break;
            }
            case MANGAFOX_LIST: {
                rowsDeleted = db.delete(MangaFoxMangaList.TABLE_NAME, s, strings);
                break;
            }
            case MANGA_EDEN: {
                rowsDeleted = db.delete(MangaEden.TABLE_NAME, s, strings);
                break;
            }
            case VIRTUAL_TABLE: {
                rowsDeleted = db.delete(VirtualTable.TABLE_NAME, s, strings);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(s != null || rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase sqLiteDatabase = myDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int rowsUpdated = 0;

        switch (match){
            case MANGA_EDEN: {
                rowsUpdated = sqLiteDatabase.update(MangaEden.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            }

            case MY_MANGA: {
                rowsUpdated = sqLiteDatabase.update(MyManga.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            }

            case MY_MANGA_WITH_NAME: {
                rowsUpdated = updateMangaByName(uri, contentValues);
                break;
            }

            case MANGAREADER_LIST: {
                rowsUpdated = sqLiteDatabase.update(MangaReaderMangaList.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            }

            case MANGAREADER_LATEST_LIST: {
                rowsUpdated = sqLiteDatabase.update(MangaReaderLatestList.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            }

            case MANGAREADER_POPULAR_LIST: {
                rowsUpdated = sqLiteDatabase.update(MangaReaderPopularList.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            }

            case MANGAFOX_LIST: {
                rowsUpdated = sqLiteDatabase.update(MangaFoxMangaList.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            }

            case VIRTUAL_TABLE: {
                rowsUpdated = sqLiteDatabase.update(VirtualTable.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            }


        }

        if(rowsUpdated != 0) getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }


}

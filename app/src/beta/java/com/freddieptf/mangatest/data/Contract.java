package com.freddieptf.mangatest.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by fred on 1/29/15.
 */
public class Contract {

    public static final String CONTENT_AUTH = "com.freddieptf.mangatest";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTH);
    public static final String PATH_MANGAREADER_LIST = "manga_reader_list";
    public static final String PATH_MANGAREADER_LATEST_LIST = "manga_reader_latest_list";
    public static final String PATH_MANGAREADER_POPULAR_LIST = "manga_reader_popular_list";
    public static final String PATH_MANGAFOX_LIST = "manga_fox_list";
    public static final String PATH_MY_MANGA = "my_manga_list";
    public static final String PATH_MANGA_EDEN = "manga_eden_list";
    public static final String PATH_VIRTUAL_TABLE = "virtual_fts3_table";


    public static class VirtualTable implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIRTUAL_TABLE).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTH + "/" + PATH_VIRTUAL_TABLE;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTH + "/" + PATH_VIRTUAL_TABLE;

        public static final String TABLE_NAME = "virtual_fts3_table";
        public static final String COLUMN_MANGA_NAME = "manga_name";
        public static final String COLUMN_MANGA_ID = "manga_id";

        public static Uri buildVirtualMangaUriWithQuery(String query){
            return CONTENT_URI.buildUpon().appendPath(query).build();
        }

        public static String getQueryFromVirtualMangaUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static Uri buildVirtualMangaUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }



    //MangaReader Manga List
    public static class MangaReaderMangaList implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MANGAREADER_LIST).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTH + "/" + PATH_MANGAREADER_LIST;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTH + "/" + PATH_MANGAREADER_LIST;

        public static final String TABLE_NAME = "mangareader_list";

        public static final String COLUMN_MANGA_NAME = "manga_name";
        public static final String COLUMN_MANGA_ID = "manga_id";

        public static Uri buildMangaListUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMangaInListWithNameUri(String mangaName){
            return CONTENT_URI.buildUpon().appendPath(mangaName).build();
        }

        public static String getMangaNameInListFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }


    }


    public static class MangaReaderLatestList implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MANGAREADER_LATEST_LIST).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTH + "/" + PATH_MANGAREADER_LATEST_LIST;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTH + "/" + PATH_MANGAREADER_LATEST_LIST;

        public static final String TABLE_NAME = "mangareader_latest_list";
        public static final String COLUMN_MANGA_NAME = "manga_name";
        public static final String COLUMN_MANGA_ID = "manga_id";
        public static final String COLUMN_CHAPTER = "manga_chapter";
        public static final String COLUMN_DATE = "date";

        public static Uri buildMangaReaderLatestListUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }


    public static class MangaReaderPopularList implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MANGAREADER_POPULAR_LIST).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTH + "/" + PATH_MANGAREADER_POPULAR_LIST;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTH + "/" + PATH_MANGAREADER_POPULAR_LIST;

        public static final String TABLE_NAME = "mangareader_popular_list";
        public static final String COLUMN_MANGA_NAME = "manga_name";
        public static final String COLUMN_CHAPTER_DETAILS = "chapter_dets";
        public static final String COLUMN_MANGA_AUTHOR = "manga_author";
        public static final String COLUMN_MANGA_GENRE = "genres";

        public static Uri buildListUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static class MangaFoxMangaList implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MANGAFOX_LIST).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTH + "/" + PATH_MANGAFOX_LIST;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTH + "/" + PATH_MANGAFOX_LIST;

        public static final String TABLE_NAME = "mangafox_list";
        public static final String COLUMN_MANGA_NAME = "manga_name";
        public static final String COLUMN_MANGA_ID = "manga_id";

        public static Uri buildMangaListUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMangaInListWithNameUri(String mangaName){
            return CONTENT_URI.buildUpon().appendPath(mangaName).build();
        }

        public static String getMangaNameInListFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }




    }

    //Store MyLibrary
    public static class MyManga implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MY_MANGA).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTH + "/" + PATH_MY_MANGA;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTH + "/" + PATH_MY_MANGA;

        public static final String TABLE_NAME = "my_manga";

        public static final String COLUMN_MANGA_NAME = "manga_name";
        public static final String COLUMN_MANGA_ID = "manga_id";
        public static final String COLUMN_MANGA_AUTHOR = "manga_author";
        public static final String COLUMN_MANGA_INFO = "manga_info";
        public static final String COLUMN_MANGA_COVER = "manga_cover_link";
        public static final String COLUMN_MANGA_STATUS = "manga_status";
        public static final String COLUMN_MANGA_LAST_UPDATE = "last_update";
        public static final String COLUMN_MANGA_LATEST_CHAPTER = "latest_chapter";
        public static final String COLUMN_MANGA_SOURCE = "manga_source";
        public static final String COLUMN_MANGA_CHAPTER_JSON = "manga_chapter_json";


        public static Uri buildMangaUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMangaWithNameUri(String mangaName){
            return CONTENT_URI.buildUpon().appendPath(mangaName).build();
        }

        public static String getMangaNameFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }


    }


    //MangaEden manga List
    public static class MangaEden implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MANGA_EDEN).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTH + "/" + PATH_MANGA_EDEN;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTH + "/" + PATH_MANGA_EDEN;

        public static final String TABLE_NAME = "manga_eden";

        public static final String COLUMN_MANGA_TITLE = "manga_title";
        public static final String COLUMN_MANGA_ID = "manga_id";
        public static final String COLUMN_MANGA_COVER = "manga_cover_link";
        public static final String COLUMN_MANGA_STATUS = "manga_status";
        public static final String COLUMN_MANGA_HITS = "hits";


        public static Uri buildMangaUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMangaWithNameUri(String mangaTitle){
            return CONTENT_URI.buildUpon().appendPath(mangaTitle).build();
        }

        public static String getMangaNameFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }


    }

}

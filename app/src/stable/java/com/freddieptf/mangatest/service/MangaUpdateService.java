package com.freddieptf.mangatest.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.freddieptf.mangatest.data.Contract.MangaFoxMangaList;
import com.freddieptf.mangatest.data.Contract.MangaReaderMangaList;
import com.freddieptf.mangatest.data.Contract.MyManga;
import com.freddieptf.mangatest.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by fred on 3/27/15.
 */
public class MangaUpdateService extends IntentService {

    final String LOG_TAG = getClass().getSimpleName();
    final public static String NOTIFY_UPDATE = "notify_update";
    Intent broadCastIntent;

    public MangaUpdateService() {
        super("MangaUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Cursor cursor = getApplicationContext().getContentResolver()
                .query(MyManga.CONTENT_URI,
                        new String[]{MyManga._ID, MyManga.COLUMN_MANGA_NAME, MyManga.COLUMN_MANGA_CHAPTER_JSON},
                        null, null, null);

        //@TODO rewrite this: use an arraylist of custom mangaObjects. Much easier to keep track of. I think..

        String[] mangaTitles;
        String[] mangaChapterJsonLocal;
        String[] mangaChapterJsonUpdate;

        if(cursor.moveToFirst()) {
            mangaTitles = new String[cursor.getCount()];
            mangaChapterJsonLocal = new String[cursor.getCount()];

            Log.d(LOG_TAG, "cursor: " + cursor.getCount());

            for(int i = 0; i < cursor.getCount(); i++){
                mangaTitles[i] = cursor.getString(1);
                mangaChapterJsonLocal[i] = cursor.getString(2);
                Log.d(LOG_TAG, mangaChapterJsonLocal[i]);
                cursor.moveToNext();
            }

            Log.d(LOG_TAG, "mangaTitles: " + mangaTitles.length);

            Cursor[] mangaIdCursors = new Cursor[mangaTitles.length];

            for(int i = 0; i < mangaTitles.length; i++){

                Uri mangaUri = MangaFoxMangaList.buildMangaInListWithNameUri(mangaTitles[i]);
                mangaIdCursors[i] = getContentResolver().query(mangaUri,
                        new String[]{MangaFoxMangaList.COLUMN_MANGA_ID},
                        null, null, null);

                if(mangaIdCursors[i].moveToFirst()){
                    Log.d(LOG_TAG, "1: " + mangaIdCursors[i].getString(0));
                }

                if(!mangaIdCursors[i].moveToFirst() || mangaIdCursors[i] == null){
                    mangaUri = MangaReaderMangaList.buildMangaInListWithNameUri(mangaTitles[i]);
                    mangaIdCursors[i] = getContentResolver().query(mangaUri,
                            new String[]{MangaReaderMangaList.COLUMN_MANGA_ID},
                            null, null, null);
                    mangaIdCursors[i].moveToFirst();
                    Log.d(LOG_TAG, "2: " + mangaIdCursors[i].getString(0));

                }

            }

            Log.d(LOG_TAG, "Cursor(mangaIds): " + mangaIdCursors.length);

            mangaChapterJsonUpdate = new String[mangaIdCursors.length];

            for(int i = 0; i < mangaIdCursors.length; i++){

                try {
                    mangaIdCursors[i].moveToFirst();

                    URL url =
                            new URL("https://doodle-manga-scraper.p.mashape.com/mangafox.me/manga/"
                                    + mangaIdCursors[i].getString(0) + "/");

                    URL url1 = new URL("https://doodle-manga-scraper.p.mashape.com/mangareader.net/manga/"
                            + mangaIdCursors[i].getString(0) + "/");


                    mangaChapterJsonUpdate[i] = getMangaJson(url);

                    if(mangaChapterJsonUpdate[i].isEmpty()){
                        mangaChapterJsonUpdate[i] = getMangaJson(url1);
                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

            Log.d(LOG_TAG, "mangaChaptersJsonUpdate: " + mangaChapterJsonUpdate.length);
            String change = "noChange";

            for(int i = 0; i < mangaChapterJsonUpdate.length; i++){

                try {
                    if(new JSONArray(mangaChapterJsonUpdate[i]).length()
                            > new JSONArray(mangaChapterJsonLocal[i]).length()){

                        JSONArray array = new JSONArray(mangaChapterJsonLocal[i]);
                        JSONArray array2 = new JSONArray(mangaChapterJsonUpdate[i]);

                        if(!array.getJSONObject((array.length() - 1)).getString("chapterId")
                                .equals(array2.getJSONObject((array2.length() - 1)).getString("chapterId"))){

                            Log.d(LOG_TAG, mangaTitles[i] + ": updated");
                            change = "change";


                            Uri uri = MyManga.buildMangaWithNameUri(mangaTitles[i]);
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(MyManga.COLUMN_MANGA_CHAPTER_JSON, mangaChapterJsonUpdate[i]);

//                            getContentResolver().update(uri, contentValues, null, null);


                            Log.d(LOG_TAG, array.getJSONObject((array.length() - 1)).toString());
                            Log.d(LOG_TAG, array2.getJSONObject((array2.length() - 1)).toString());

                            String oldId = array.getJSONObject((array.length() - 1)).getString("chapterId");
                            String newId = array2.getJSONObject((array2.length() - 1)).getString("chapterId");
                            double o =  Double.parseDouble(oldId);
                            double n = Double.parseDouble(newId);
                            int updateMargin = (int) (n - o);
                            Log.d(LOG_TAG, oldId + " " + newId);

                            Utilities.writeMangaPageToPrefs(getApplicationContext(), mangaTitles[i], updateMargin);

                        }



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            broadCastIntent = new Intent(LOG_TAG);
            broadCastIntent.putExtra(NOTIFY_UPDATE, change);
            sendBroadcast(broadCastIntent);


        }

        cursor.close();

    }

    private String getMangaJson(URL url){

        String result = "";
        Log.d(LOG_TAG, "url: " + url.toString());

        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.addRequestProperty("X-Mashape-Key", "8Fp0bd39gLmshw7qSKtW61cjlK6Ip1V1Z5Fjsnhpy813RcQflk");
            httpURLConnection.connect();

            if(httpURLConnection.getResponseCode() != 200) return "";

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();

            String line, r;

            while ((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }

            r = stringBuilder.toString();

            JSONObject object = new JSONObject(r);

            if(object.has("chapters")){
                result = object.getJSONArray("chapters").toString();
            }


        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }


        return result;
    }
}

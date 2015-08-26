package com.freddieptf.mangatest.MangaFoxAndReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.Contract;
import com.freddieptf.mangatest.mainUi.fragments.MangaDetailsFragment;
import com.freddieptf.mangatest.utils.Utilities;

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
 * Created by fred on 1/30/15.
 */
public class FetchManga {

    public static class getMangaFromTitle extends AsyncTask<String, Void, Integer>{

        Context context;
        Handler handler = new Handler();
        public final Boolean DEBUG = true;
        String result;

        public getMangaFromTitle(Context context){
            this.context = context;
        }

        String LOG_TAG = getClass().getSimpleName();

        HttpURLConnection httpURLConnection;
        HttpResponseCache httpResponseCache;
        BufferedReader bufferedReader;

        @Override
        protected void onPostExecute(Integer internets) {
            super.onPostExecute(internets);
            if(internets == 1){
                MangaDetailsFragment.populateViewsWithData.execute();
            }
        }

        @Override
        protected Integer doInBackground(String... strings) {
            try {
                URL baseUrl;

                if(Utilities.getCurrentSource(context).equals(context.getString(R.string.pref_manga_reader))){
                    baseUrl = new URL
                            ("https://doodle-manga-scraper.p.mashape.com/mangareader.net/manga/" + strings[0] + "/");
                }else {
                    baseUrl = new URL
                            ("https://doodle-manga-scraper.p.mashape.com/mangafox.me/manga/" + strings[0] + "/");
                }

                httpURLConnection = (HttpURLConnection)baseUrl.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.addRequestProperty("X-Mashape-Key", "8Fp0bd39gLmshw7qSKtW61cjlK6Ip1V1Z5Fjsnhpy813RcQflk");
                httpURLConnection.connect();

                int statusCode = httpURLConnection.getResponseCode();

                if(statusCode != 200) return -1;



                Log.i(LOG_TAG, "Status Code: " + statusCode);

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line);
                }

                result = stringBuilder.toString();

                Log.d(LOG_TAG, result);


                processResults(result);



            } catch (MalformedURLException e) {
                if(DEBUG) e.printStackTrace();

                Log.d(LOG_TAG, e.getMessage());

            } catch (IOException e) {
                if(DEBUG) e.printStackTrace();

                Log.d(LOG_TAG, e.getMessage());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "No internet Connection", Toast.LENGTH_LONG).show();
                    }
                });

                return 0;

            }finally {
                if(httpURLConnection != null) httpURLConnection.disconnect();

                if(bufferedReader != null) try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            return 1;
        }


        public void processResults(String string){
            String MANGA_NAME = "name",
                    MANGA_AUTHOR = "author",
                    MANGA_INFO = "info",
                    MANGA_STATUS = "status",
                    MANGA_COVER = "cover",
                    MANGA_LAST_UPDATE = "lastUpdate",
                    MANGA_CHAPTERS = "chapters";


            try {
                String name, status, info, cover, author, chapters, lastUpdate;
                JSONObject mainJsonObject = new JSONObject(string);

                name = mainJsonObject.getString(MANGA_NAME);

                if(!mainJsonObject.has(MANGA_STATUS))status = "wakaranai";
                else status = mainJsonObject.getString(MANGA_STATUS);

                if(!mainJsonObject.has(MANGA_INFO))info = "No description";
                else info = mainJsonObject.getString(MANGA_INFO);

                if(!mainJsonObject.has(MANGA_AUTHOR))author = "Some Mangaka";
                else author = mainJsonObject.getJSONArray(MANGA_AUTHOR).getString(0);

                if(!mainJsonObject.has(MANGA_LAST_UPDATE)) lastUpdate = "";
                else lastUpdate = mainJsonObject.getString(MANGA_LAST_UPDATE);

                cover = mainJsonObject.getString(MANGA_COVER);
                chapters = mainJsonObject.getJSONArray(MANGA_CHAPTERS).toString();

                if(DEBUG) {
                    Log.d("Manga Chapters: ", chapters);
                    Log.d("Manga author: ", author);
                    Log.d("Manga lastUpdate: ", lastUpdate);
                    Log.d("Manga Name: ", name);
                    Log.d("Manga status: ", status);
                    Log.d("Manga info: ", info);
                    Log.d("Manga cover: ", cover);
                }

                if(info == null || author == null || name == null){
                    name = "...";
                    info = "No info.";
                    author = "Some great mangaka";

                }

                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.MyManga.COLUMN_MANGA_NAME, name);
                contentValues.put(Contract.MyManga.COLUMN_MANGA_AUTHOR, author);
                contentValues.put(Contract.MyManga.COLUMN_MANGA_INFO, info);
                contentValues.put(Contract.MyManga.COLUMN_MANGA_COVER, cover);
                contentValues.put(Contract.MyManga.COLUMN_MANGA_STATUS, status);
                contentValues.put(Contract.MyManga.COLUMN_MANGA_CHAPTER_JSON, chapters);
                contentValues.put(Contract.MyManga.COLUMN_MANGA_LAST_UPDATE, lastUpdate);

                Uri uri = context.getContentResolver().insert(Contract.MyManga.CONTENT_URI, contentValues);




                if(uri != null) {
                    Log.i("URI", uri.getPath());
                    Cursor cursor = context.getContentResolver().query(
                            Contract.MyManga.CONTENT_URI.buildUpon().appendPath(uri.getPath()).build(), null, null, null, null);
                    if(cursor.moveToFirst()){
                        Log.d("Cursor test: ", "SOMETHING BOII");
                        Log.d("Cursor test: ", cursor.getString(0) + cursor.getString(1));
                    }

                    cursor.close();
                }else{
                    Log.d("Cursor test: ", "failed");
                }





            } catch (JSONException e) {
                e.printStackTrace();
            }


        }



    }




    public static class getMangaFromID extends AsyncTask<String, Void, Void>{

        Context context;
        String LOG_TAG = getClass().getSimpleName();
        HttpURLConnection httpURLConnection;
        BufferedReader bufferedReader;

        public getMangaFromID(Context context){
            this.context = context;

        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                URL baseUrl =  new URL("https://www.mangaeden.com/api/manga/" + strings[0] +"/");
                httpURLConnection = (HttpURLConnection)baseUrl.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                int statusCode = httpURLConnection.getResponseCode();
                if(statusCode != 200) return null;
                Log.d(LOG_TAG, statusCode + " ");

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                StringBuilder stringBuilder = new StringBuilder();

                while((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line);
                }

                String results = stringBuilder.toString();
                Log.d(LOG_TAG, results);

                processResults(results);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static void processResults(String string){
            String MANGA_NAME = "title",
                    MANGA_AUTHOR = "author",
                    MANGA_INFO = "description",
                    MANGA_STATUS = "status",
                    MANGA_COVER = "image",
                    MANGA_CHAPTERS = "chapters";

            String coverImgDownloadSiteUrl = "https://cdn.mangaeden.com/mangasimg/";


            try {
                JSONObject mainJsonObject = new JSONObject(string);
                String name = mainJsonObject.getString(MANGA_NAME);
                String status = mainJsonObject.getString(MANGA_STATUS);
                String info = mainJsonObject.getString(MANGA_INFO);
                String cover = coverImgDownloadSiteUrl + mainJsonObject.getString(MANGA_COVER);
                String author = mainJsonObject.getString(MANGA_AUTHOR);
                String chapters = mainJsonObject.getJSONArray(MANGA_CHAPTERS).toString();

                Log.d("Manga Chapters: ", chapters);
                Log.d("Manga author: ", author);
                Log.d("Manga Name: ", name);
                Log.d("Manga status: ", status);
                Log.d("Manga info: ", info);
                Log.d("Manga cover: ", cover);

//            ContentValues contentValues = new ContentValues();
//            contentValues.put(Contract.MyManga.COLUMN_MANGA_NAME, name);
//            contentValues.put(Contract.MyManga.COLUMN_MANGA_AUTHOR, author);
//            contentValues.put(Contract.MyManga.COLUMN_MANGA_INFO, info);
//            contentValues.put(Contract.MyManga.COLUMN_MANGA_COVER, cover);
//            contentValues.put(Contract.MyManga.COLUMN_MANGA_STATUS, status);
//            contentValues.put(Contract.MyManga.COLUMN_MANGA_CHAPTER_JSON, chapters);





            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }








}

package com.freddieptf.mangatest.MangaEden;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.AsyncTask;
import android.util.Log;

import com.freddieptf.mangatest.data.Contract;

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
import java.util.Vector;

/**
 * Created by fred on 2/8/15.
 */
public class FetchMangaList extends AsyncTask<Void, Void, Void> {

    HttpURLConnection httpURLConnection = null;
    BufferedReader bufferedReader = null;
    Context context;
    boolean DEBUG = true;

    public FetchMangaList(Context context){
        this.context = context;
    }



    @Override
    protected Void doInBackground(Void... voids) {
        getList();
        return null;
    }


    //Getting full manga list from Manga Eden & inserting to database through processListJson().
    // Should move this to a service
    private void getList(){
        URL baseUrl = null;
        String result = null;
//        String url1 = "https://www.mangaeden.com/api/list/0/?p=0&i=500";
        String url2 = "https://www.mangaeden.com/api/list/0/";

        try {
            baseUrl = new URL(url2);
            httpURLConnection = (HttpURLConnection)baseUrl.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            int statusCode = httpURLConnection.getResponseCode();

            if(statusCode != 200) return;

            Log.i("Status Code: ", statusCode + " ");


            InputStream inputStream = httpURLConnection.getInputStream();

            if(inputStream == null) return;
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();

            String line = null;

            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }

            result = stringBuilder.toString();

            Log.i("List Result: ", result);




        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(httpURLConnection != null){
                    httpURLConnection.disconnect();
                }

                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        processListJson(result);


    }

    private void processListJson(String results){
        final String MANGA = "manga";
        final String MANGA_TITLE = "t";
        final String MANGA_ID = "i";
        final String MANGA_HITS = "h";
        final String MANGA_IMAGE = "im";
        final String MANGA_STATUS = "s";

        try {
            JSONObject jsonObject = new JSONObject(results);
            JSONArray mangaJsonArray = jsonObject.getJSONArray(MANGA);

            Vector<ContentValues> contentValuesVector = new Vector<ContentValues>(mangaJsonArray.length());

            Log.i("MangaJsonArray size: ", mangaJsonArray.length() + " ");

            for(int i = 0; i < mangaJsonArray.length(); i++){
                String mangaTitle, mangaImage, mangaStatus, mangaId;
                int  mangaHits;

                JSONObject object = mangaJsonArray.getJSONObject(i);

                mangaTitle = object.getString(MANGA_TITLE);
                mangaHits = object.getInt(MANGA_HITS);
                mangaId = object.getString(MANGA_ID);
                mangaImage = object.getString(MANGA_IMAGE);
                mangaStatus = object.getString(MANGA_STATUS);


                ContentValues contentValues = new ContentValues();

                contentValues.put(Contract.MangaEden.COLUMN_MANGA_TITLE, mangaTitle);
                contentValues.put(Contract.MangaEden.COLUMN_MANGA_HITS, mangaHits);
                contentValues.put(Contract.MangaEden.COLUMN_MANGA_ID, mangaId);
                contentValues.put(Contract.MangaEden.COLUMN_MANGA_STATUS, mangaStatus);
                contentValues.put(Contract.MangaEden.COLUMN_MANGA_COVER, mangaImage);

                contentValuesVector.add(contentValues);


            }

            if(contentValuesVector.size() > 0) {
                ContentValues[] contentValueses = new ContentValues[contentValuesVector.size()];
                contentValuesVector.toArray(contentValueses);
                int rowsInserted = context.getContentResolver().bulkInsert(Contract.MangaEden.CONTENT_URI, contentValueses);
                Log.v("Get List: ", "inserted " + rowsInserted + " rows of data");

                if(DEBUG){
                    Cursor cursor = context.getContentResolver().query(Contract.MangaEden.CONTENT_URI, null,
                            null,
                            null,
                            null);
                    if(cursor.moveToFirst()){
                        ContentValues result = new ContentValues();
                        DatabaseUtils.cursorRowToContentValues(cursor, result);
                        Log.v("Get List", "Query succeeded! **********");
                        for(String key : result.keySet()){
                            Log.v("Get List", key + ": " + result.getAsString(key));
                        }
                    } else {
                        Log.v("Get List", "Query Failed dammit!");
                    }
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}

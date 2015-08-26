package com.freddieptf.mangatest.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.freddieptf.mangatest.data.Contract;
import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by fred on 4/10/15.
 */
public class DownloadMangaDatabase extends Service {

    final String LOG_TAG = getClass().getSimpleName();
    String[] sources = new String[]{"MangaReader",
            "MangaFox"};
    int selection = -1;
    public static final String OP = "operation";
    public static final String STATUS = "status";
    public static final String FIX_SELECTION = "fix_selection";
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;
    final int NOTIFICATION_ID = 1002;
    final Object obj = new Object();

    @Override
    public void onCreate() {
        super.onCreate();
        builder = new NotificationCompat.Builder(this);
        notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(Utilities.isOnline(this)){

            synchronized (obj) {
                selection = getIntExtra(intent);
            }

            Log.i(LOG_TAG, "onHandleIntent: YEZ internets! Selection: " + selection);

            new GetAndProcessJson().execute();

        }else {
            Log.i(LOG_TAG, "onHandleIntent: NO internets");
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public DownloadMangaDatabase() {
        super();
    }


    private int getIntExtra(Intent intent) {
        if (intent.getExtras() != null) {
            return intent.getIntExtra(FIX_SELECTION, 1);
        }
        return -1;
    }

    private void deleteAll(){
        getContentResolver().delete(Contract.VirtualTable.CONTENT_URI, null, null);
        getContentResolver().delete(Contract.MangaFoxMangaList.CONTENT_URI, null, null);
        getContentResolver().delete(Contract.MangaReaderMangaList.CONTENT_URI, null, null);
    }

    private void init(){
        builder.setContentTitle(getString(R.string.app_name))
                .setProgress(0, 0, true)
                .setContentText("Preparing to download databases")
                .setSmallIcon(R.drawable.ic_stat_maps_local_library);

        startForeground(NOTIFICATION_ID, builder.build());
    }

    class GetAndProcessJson extends AsyncTask<Void, Void, String[]>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            init();
        }

        @Override
        protected String[] doInBackground(Void... voids) {
            //gets the Json and populates the database
            return getJson();
        }

        @Override
        protected void onPostExecute(final String[] strings) {
            super.onPostExecute(strings);

            if(strings != null) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PopulateDatabase(strings);
                        addStuffToVirtualTable();
                    }

                });
                thread.start();
            }

        }
    }


    private String[] getJson() {
        URL mangaFox_baseUrl, mangaReader_baseUrl;
        URL[] urls;
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        final String LOG_TAG = getClass().getSimpleName();
        String[] results = new String[]{};

        int j, k;

        try {
            mangaFox_baseUrl = new URL("https://doodle-manga-scraper.p.mashape.com/mangafox.me/");
            mangaReader_baseUrl = new URL("https://doodle-manga-scraper.p.mashape.com/mangareader.net/");

            urls = new URL[]{mangaReader_baseUrl, mangaFox_baseUrl};

            if(selection == 0){
                j = 0;
                k = 1;
                results = new String[1];
            }else if(selection == 1){
                j = 1;
                k = urls.length;
                results = new String[1];
            }else {
                j = 0;
                k = urls.length;
                results = new String[2];
            }

            builder.setContentText("Downloading databases...")
                    .setProgress(0, 0, true);
            notificationManager.notify(NOTIFICATION_ID, builder.build());

            for (int i = j; i < k; i++) {
                URL base = urls[i];

                Intent intent = new Intent(LOG_TAG);
                intent.putExtra(OP, "Downloading...");
                intent.putExtra(STATUS, "Downloading " + sources[i] + " Manga database.");
                sendBroadcast(intent);

                httpURLConnection = (HttpURLConnection) base.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.addRequestProperty("X-Mashape-Key", "8Fp0bd39gLmshw7qSKtW61cjlK6Ip1V1Z5Fjsnhpy813RcQflk");
                httpURLConnection.connect();

                int StatusCode = httpURLConnection.getResponseCode();

                if (StatusCode != 200) return null;

                Log.i(LOG_TAG + "Status Code ", " " + StatusCode);

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                String result;
                StringBuilder stringBuilder = new StringBuilder();

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                result = stringBuilder.toString();

                Log.d(LOG_TAG + "results: ", result);

                results[selection == 1 ? 0 : i] = result;

            }


            if(selection == 0 && results[0] != null && !results[0].isEmpty()){
                getContentResolver().delete(Contract.MangaReaderMangaList.CONTENT_URI, null, null);
            }else if (selection == 1 && results[0] != null && !results[0].isEmpty()){
                getContentResolver().delete(Contract.MangaFoxMangaList.CONTENT_URI, null, null);
                getContentResolver().delete(Contract.VirtualTable.CONTENT_URI, null, null);
            }else if(selection == -1){
                deleteAll();
            }


            builder.setContentText("Download done. Unpacking databases...");
            notificationManager.notify(NOTIFICATION_ID, builder.build());


        } catch (IOException e) {
            e.printStackTrace();
            results = null;
        } finally {
            if (httpURLConnection != null) httpURLConnection.disconnect();
            try {
                if (bufferedReader != null) bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return results;

    }

    private void PopulateDatabase(String[] results) {
        final String MANGA_ID = "mangaId";
        final String MANGA_NAME = "name";
        Uri[] contentUri = new Uri[]{Contract.MangaReaderMangaList.CONTENT_URI, Contract.MangaFoxMangaList.CONTENT_URI};
        Vector<ContentValues> contentValueses = new Vector<>();

        Log.d(LOG_TAG, "results length: " + results.length);

        try {

            for (int y = 0; y < results.length; y++) {

                Intent intent = new Intent(LOG_TAG);
                intent.putExtra(OP, "Unpacking databases...");
                intent.putExtra(STATUS, "Processing " + sources[selection == 1 ? 1 : y] + " Manga database.");
                sendBroadcast(intent);


                JSONArray mainArray = new JSONArray(results[y]);

                for (int i = 0; i < mainArray.length(); i++) {
                    JSONObject object = mainArray.getJSONObject(i);

                    String mangaId = object.getString(MANGA_ID);
                    String mangaName = object.getString(MANGA_NAME);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contract.MangaFoxMangaList.COLUMN_MANGA_NAME, mangaName);
                    contentValues.put(Contract.MangaFoxMangaList.COLUMN_MANGA_ID, mangaId);

                    contentValueses.add(contentValues);

                }

                Log.i(LOG_TAG, "ContentValueses Size: " + contentValueses.size());

                if (contentValueses.size() > 0) {
                    ContentValues[] contentValuesArray = new ContentValues[contentValueses.size()];
                    contentValueses.toArray(contentValuesArray);

                    Log.d(LOG_TAG, "contentValues: " + contentValuesArray.length);

                    Uri uri;
                    if(selection == 0){
                        uri = contentUri[0];
                    }else if(selection == 1){
                        uri = contentUri[1];
                    }else{
                        uri = contentUri[y];
                    }

                    Log.d(LOG_TAG, "Uri: " + uri.toString());
                    int rowsInserted = getContentResolver().bulkInsert(uri, contentValuesArray);
                    if (y == 0) {
                        getContentResolver().bulkInsert(Contract.VirtualTable.CONTENT_URI, contentValuesArray);
                    }
                    Log.i("rows inserted: ", "rows: " + rowsInserted);
                    contentValueses.clear();

                }

            }

            builder.setProgress(0, 0, false)
            .setContentText("Done downloading and unpacking the Manga databases.");
            notificationManager.notify(NOTIFICATION_ID, builder.build());

        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
            builder.setContentText("Looks like something went wrong: " + e.getMessage());
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } finally {
            contentValueses.clear();
        }

    }

    public void addStuffToVirtualTable() {
        Vector<ContentValues> contentValueses = new Vector<>();

        if(selection != 0) {
            Cursor cursor = getContentResolver().query(Contract.MangaFoxMangaList.CONTENT_URI, null, null, null, null);

            if (cursor.moveToFirst()) {
                Log.d(LOG_TAG, " " + cursor.getCount());

                try {

                    for (int i = 0; i < cursor.getCount(); i++) {

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Contract.VirtualTable.COLUMN_MANGA_NAME, cursor.getString(1));
                        contentValues.put(Contract.VirtualTable.COLUMN_MANGA_ID, cursor.getString(2));
                        contentValueses.add(contentValues);

                        cursor.moveToNext();
                    }

                } catch (CursorIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

            }

            ContentValues[] contentValues = new ContentValues[contentValueses.size()];
            contentValueses.toArray(contentValues);

            int rowsInserted = getContentResolver().bulkInsert(Contract.VirtualTable.CONTENT_URI, contentValues);

            Log.d(LOG_TAG, "Virtual rows inserted: " + rowsInserted);

            cursor.close();

        }

    }

}

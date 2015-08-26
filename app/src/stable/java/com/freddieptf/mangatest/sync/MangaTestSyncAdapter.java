package com.freddieptf.mangatest.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.Contract;
import com.freddieptf.mangatest.mainUi.MainActivity;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fred on 5/2/15.
 */
public class MangaTestSyncAdapter extends AbstractThreadedSyncAdapter {

    final String LOG_TAG = getClass().getSimpleName();
    final int NOTIFICATION_ID = 1000;
    final public static String NOTIFY_UPDATE = "notify_update";
    public static final int SYNC_INTERVAL = 60 * 720; // 12 hours...i think
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 2;
    ContentResolver mContentResolver;

    String ls;


    public MangaTestSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();

    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {

        Cursor cursor = mContentResolver
                .query(Contract.MyManga.CONTENT_URI,
                        new String[]{Contract.MyManga._ID, Contract.MyManga.COLUMN_MANGA_NAME, Contract.MyManga.COLUMN_MANGA_CHAPTER_JSON},
                        null, null, null);

        String[] mangaTitles;
        String[] mangaChapterJsonLocal;
        String[] mangaChapterJsonUpdate;

        if (cursor.moveToFirst()) {
            mangaTitles = new String[cursor.getCount()];
            mangaChapterJsonLocal = new String[cursor.getCount()];

            Log.d(LOG_TAG, "cursor: " + cursor.getCount());

            for (int i = 0; i < cursor.getCount(); i++) {
                mangaTitles[i] = cursor.getString(1);
                mangaChapterJsonLocal[i] = cursor.getString(2);
                Log.d(LOG_TAG, mangaChapterJsonLocal[i]);
                cursor.moveToNext();
            }

            Log.d(LOG_TAG, "mangaTitles: " + mangaTitles.length);

            Cursor[] mangaIdCursors = new Cursor[mangaTitles.length];

            for (int i = 0; i < mangaTitles.length; i++) {

                Uri mangaUri = Contract.MangaFoxMangaList.buildMangaInListWithNameUri(mangaTitles[i]);
                mangaIdCursors[i] = mContentResolver.query(mangaUri,
                        new String[]{Contract.MangaFoxMangaList.COLUMN_MANGA_ID},
                        null, null, null);

                if (mangaIdCursors[i].moveToFirst()) {
                    Log.d(LOG_TAG, "1: " + mangaIdCursors[i].getString(0));
                }

                if (!mangaIdCursors[i].moveToFirst() || mangaIdCursors[i] == null) {
                    mangaUri = Contract.MangaReaderMangaList.buildMangaInListWithNameUri(mangaTitles[i]);
                    mangaIdCursors[i] = mContentResolver.query(mangaUri,
                            new String[]{Contract.MangaReaderMangaList.COLUMN_MANGA_ID},
                            null, null, null);
                    mangaIdCursors[i].moveToFirst();
                    Log.d(LOG_TAG, "2: " + mangaIdCursors[i].getString(0));

                }

            }

            Log.d(LOG_TAG, "Cursor(mangaIds): " + mangaIdCursors.length);

            mangaChapterJsonUpdate = new String[mangaIdCursors.length];
            String[] coverUrl = new String[mangaIdCursors.length];

            for (int i = 0; i < mangaIdCursors.length; i++) {

                try {
                    mangaIdCursors[i].moveToFirst();

                    URL url =
                            new URL("https://doodle-manga-scraper.p.mashape.com/mangafox.me/manga/"
                                    + mangaIdCursors[i].getString(0) + "/");

                    URL url1 = new URL("https://doodle-manga-scraper.p.mashape.com/mangareader.net/manga/"
                            + mangaIdCursors[i].getString(0) + "/");


                    mangaChapterJsonUpdate[i] = getMangaJson(url);

                    if (mangaChapterJsonUpdate[i].isEmpty()){
                        mangaChapterJsonUpdate[i] = getMangaJson(url1);
                    }




                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

            Log.d(LOG_TAG, "mangaChaptersJsonUpdate: " + mangaChapterJsonUpdate.length);
            String change = "noChange";
            int updates = 0;
            List<String> updatedMangalist = new ArrayList<>();

            for (int i = 0; i < mangaChapterJsonUpdate.length; i++) {

                try {
                    if (new JSONArray(mangaChapterJsonUpdate[i]).length()
                            > new JSONArray(mangaChapterJsonLocal[i]).length()) {

                        JSONArray array = new JSONArray(mangaChapterJsonLocal[i]);
                        JSONArray array2 = new JSONArray(mangaChapterJsonUpdate[i]);

                        if (!array.getJSONObject((array.length() - 1)).getString("chapterId")
                                .equals(array2.getJSONObject((array2.length() - 1)).getString("chapterId"))) {

                            Log.d(LOG_TAG, array.getJSONObject((array.length() - 1)).toString());
                            Log.d(LOG_TAG, array2.getJSONObject((array2.length() - 1)).toString());

                            String oldId = array.getJSONObject((array.length() - 1)).getString("chapterId");
                            String newId = array2.getJSONObject((array2.length() - 1)).getString("chapterId");
                            double o = Double.parseDouble(oldId);
                            double n = Double.parseDouble(newId);

                            if (n > o) {

                                Log.d(LOG_TAG, mangaTitles[i] + ": updated");
                                change = "change";

                                int updateMargin = (int) (n - o);
                                Log.d(LOG_TAG, oldId + " " + newId);

                                Uri uri = Contract.MyManga.buildMangaWithNameUri(mangaTitles[i]);
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(Contract.MyManga.COLUMN_MANGA_CHAPTER_JSON, mangaChapterJsonUpdate[i]);

                                mContentResolver.update(uri, contentValues, null, null);
                                mContentResolver.notifyChange(uri, null, false);

                                Cursor c = mContentResolver.query(uri, new String[]{Contract.MyManga.COLUMN_MANGA_ID},
                                        null, null, null);

                                c.moveToFirst();
                                String result = c.getString(0);
                                Utilities.writeMangaPageToPrefs(getContext(), result, updateMargin);
                                c.close();

                                updates++;
                                updatedMangalist.add(mangaTitles[i]);

                            }

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            mContentResolver.notifyChange(Contract.MyManga.CONTENT_URI, null, false);

            //@TODO broadcast receiver in main activity just incase it updates while the app is active..SET THIS UP
            Intent broadCastIntent = new Intent(LOG_TAG);
            broadCastIntent.putExtra(NOTIFY_UPDATE, change);
            getContext().sendBroadcast(broadCastIntent);

            if (updates > 0) notify(updates + " Manga in your library have been updated.", updatedMangalist);

        }

        cursor.close();


    }


    private String getMangaJson(URL url) {

        String result = "";
        Log.d(LOG_TAG, "url: " + url.toString());

        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.addRequestProperty("X-Mashape-Key", "8Fp0bd39gLmshw7qSKtW61cjlK6Ip1V1Z5Fjsnhpy813RcQflk");
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() != 200) return "";

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();

            String line, r;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            r = stringBuilder.toString();

            JSONObject object = new JSONObject(r);

            if (object.has("chapters")) result = object.getJSONArray("chapters").toString();


        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }


        return result;
    }


    private void notify(String contextText, List<String> list) {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setAction(MainActivity.ACTION_UPDATE);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String bigString = "Updated: ";
        int i = 0;
        for(String s : list) {
            bigString = bigString.concat(s + (i != list.size() ? ", " : "."));
            i++;
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getContext())
                        .setSmallIcon(R.drawable.ic_stat_maps_local_library)
                        .setContentTitle("Manga Updates")
                        .setContentText(contextText)
                        .setNumber(list.size())
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(pendingIntent)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(bigString))
                        .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)
                getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.contentAuthority);

        Bundle bundle = new Bundle();
        bundle.putBoolean(authority, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(bundle).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, bundle, syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.contentAuthority), bundle);
    }

    public static Account getSyncAccount(Context context) {

        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account("MangaTest", context.getString(R.string.account_type));

        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;

    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MangaTestSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.contentAuthority), true);

        /*
         * Finally, let's do a sync to get things started

            syncImmediately(context);

            *
            */


    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }


}

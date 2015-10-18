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
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.api.GetManga;
import com.freddieptf.mangatest.api.helperInterfaces.GetListListener;
import com.freddieptf.mangatest.api.mangareader.MangaReader;
import com.freddieptf.mangatest.api.workers.InsertCall;
import com.freddieptf.mangatest.beans.MangaDetailsObject;
import com.freddieptf.mangatest.beans.MangaLatestInfoBean;
import com.freddieptf.mangatest.data.Contract;
import com.freddieptf.mangatest.mainUi.MainActivity;
import com.freddieptf.mangatest.utils.MyColorUtils;
import com.freddieptf.mangatest.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fred on 5/2/15.
 */
public class MangaTestSyncAdapter extends AbstractThreadedSyncAdapter {

    final String LOG_TAG = getClass().getSimpleName();
    final int NOTIFICATION_ID = 1000;
    final public static String INTENT_FILTER = "com.mangatest.SYNC";
    final public static String UPDATE_LIST_EXTRA = "list_extra";
    public static final int SYNC_INTERVAL = 60 * 720; // 12 hours...i think
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 2;

    final int MANGA_ID = 1;
    final int MANGA_NAME = 2;
    final int MANGA_CHAPTER_JSON = 3;
    final int MANGA_SOURCE = 4;
    final int MANGA_COVER = 5;

    public MangaTestSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        checkForUpdates();
        updateLatestMangaList();
    }

    private void checkForUpdates() {
        List<String> updated = new ArrayList<>();
        GetManga getManga = new GetManga(getContext());

        Cursor cursor = getContext().getContentResolver().query(
                Contract.MyManga.CONTENT_URI,
                new String[]{Contract.MyManga._ID,
                        Contract.MyManga.COLUMN_MANGA_ID,
                        Contract.MyManga.COLUMN_MANGA_NAME,
                        Contract.MyManga.COLUMN_MANGA_CHAPTER_JSON,
                        Contract.MyManga.COLUMN_MANGA_SOURCE,
                        Contract.MyManga.COLUMN_MANGA_COVER},
                null, null, null);


        if(cursor != null && cursor.moveToFirst()){
            do{
                try {
                    MangaDetailsObject detailsObject =
                            getManga.getManga(cursor.getString(MANGA_ID), cursor.getString(MANGA_SOURCE));

                    if(detailsObject != null) {
                        JSONArray localChapters = new JSONArray(cursor.getString(MANGA_CHAPTER_JSON));
                        JSONArray freshChapters = new JSONArray(detailsObject.getChapters());

                        String last_local = localChapters.getJSONObject(localChapters.length() - 1).getString("chapterId");
                        String last_fresh = freshChapters.getJSONObject(freshChapters.length() - 1).getString("chapterId");

                        Uri uri = Contract.MyManga.buildMangaWithNameUri(detailsObject.getName());

                        if (!last_local.equals(last_fresh)) {
                            ContentValues cv = new ContentValues();
                            cv.put(Contract.MyManga.COLUMN_MANGA_CHAPTER_JSON, detailsObject.getChapters());
                            getContext().getContentResolver().update(uri, cv, null, null);
                            getContext().getContentResolver().notifyChange(uri, null);

                            //get number of updates if user didn't go through them already
                            int updates = Utilities.readMangaPageFromPrefs(getContext(), cursor.getString(MANGA_ID));
                            updates += (Integer.parseInt(last_fresh.trim()) - Integer.parseInt(last_local.trim()));
                            Utilities.writeMangaPageToPrefs(getContext(), cursor.getString(MANGA_ID), updates);

                            updated.add(detailsObject.getName());
                            Utilities.Log(LOG_TAG, detailsObject.getName() + ": updated");
                        } else {
                            Utilities.Log(LOG_TAG, detailsObject.getName() + ": No update");
                        }

                        if (!cursor.getString(MANGA_COVER).equals(detailsObject.getCover())) {
                            ContentValues cv = new ContentValues();
                            cv.put(Contract.MyManga.COLUMN_MANGA_COVER, detailsObject.getCover());
                            getContext().getContentResolver().update(uri, cv, null, null);
                        }
                    } else break;

                }catch (JSONException e){
                    e.printStackTrace();
                }


            } while(cursor.moveToNext());

            cursor.close();
            if(updated.size() > 0) notifyUser(updated);

            Intent intent = new Intent(INTENT_FILTER);
            intent.putExtra(UPDATE_LIST_EXTRA, updated.toArray());
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
            Utilities.Log(LOG_TAG, "Updates: " + updated.size());

        }
    }

    private void updateLatestMangaList(){
        final Cursor cursor = getContext().getContentResolver().query(
                Contract.MangaReaderLatestList.CONTENT_URI,
                new String[]{Contract.MangaReaderLatestList._ID, //0
                        Contract.MangaReaderLatestList.COLUMN_MANGA_NAME, //1
                        Contract.MangaReaderLatestList.COLUMN_CHAPTER}, //2
                null, null, null);

        if(cursor != null && cursor.moveToFirst()) {
            MangaReader mangaReader = new MangaReader();
            mangaReader.getLatestList(new GetListListener() {
                @Override
                public void onGetList(@NonNull List list) {
                    if(list.size() > 0) {
                        MangaLatestInfoBean latestInfoBean = (MangaLatestInfoBean) list.get(0);
                        if (!cursor.getString(1).equals(latestInfoBean.getMangaTitle())) {
                            InsertCall insertCall = new InsertCall(list, Contract.MangaReaderLatestList.CONTENT_URI, getContext());
                            int rows = getContext().getContentResolver().delete(Contract.MangaReaderLatestList.CONTENT_URI, null, null);
                            Utilities.Log(LOG_TAG, "latestRowsDeleted: " + rows);
                            insertCall.start();
                            cursor.close();
                        }
                    }
                }
            });
        }
    }

    private void notifyUser(List<String> list) {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setAction(MainActivity.ACTION_UPDATE);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String bigString = "Updated: ";
        String contextText = list.size() + (list.size() == 1 ? " Manga in your library has been updated." :
                " Manga in your library have been updated.");
        int i = 1;
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
                        .setColor(new MyColorUtils(getContext()).getAccentColor())
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

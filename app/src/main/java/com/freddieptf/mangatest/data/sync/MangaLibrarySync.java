package com.freddieptf.mangatest.data.sync;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.MangaDetailsRepository;
import com.freddieptf.mangatest.data.local.Contract;
import com.freddieptf.mangatest.data.model.Chapter;
import com.freddieptf.mangatest.data.model.MangaDetails;
import com.freddieptf.mangatest.data.remote.MangaFox;
import com.freddieptf.mangatest.data.remote.MangaReader;
import com.freddieptf.mangatest.ui.MainActivity;
import com.freddieptf.mangatest.utils.MyColorUtils;
import com.freddieptf.mangatest.utils.Utilities;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by freddieptf on 22/09/16.
 */

public class MangaLibrarySync extends Job {

    public static final String TAG = "MangaLibrarySyncJobTAG";
    final public static String UPDATE_LIST_EXTRA = "list_extra";
    public static String INTENT_FILTER = "inini";
    private final int NOTIFICATION_ID = 1000;
    private final int MANGA_ID = 1;
    private final int MANGA_NAME = 2;
    private final int MANGA_CHAPTER_JSON = 3;
    private final int MANGA_SOURCE = 4;
    private final int MANGA_COVER = 5;

    public static void scheduleJob() {
        JobManager.instance().cancelAllForTag(MangaLibrarySync.TAG);
        new JobRequest.Builder(MangaLibrarySync.TAG)
                .setPeriodic(TimeUnit.HOURS.toMillis(8))
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setRequirementsEnforced(true)
                .setPersisted(true)
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        return checkForUpdates(getContext());
    }

    private Job.Result checkForUpdates(Context context) {
        List<String> updated = new ArrayList<>();
        MangaDetailsRepository repository = MangaDetailsRepository.getInstance(context);

        Cursor cursor = context.getContentResolver().query(
                Contract.MyManga.CONTENT_URI,
                new String[]{Contract.MyManga._ID,
                        Contract.MyManga.COLUMN_MANGA_ID,
                        Contract.MyManga.COLUMN_MANGA_NAME,
                        Contract.MyManga.COLUMN_MANGA_CHAPTER_JSON,
                        Contract.MyManga.COLUMN_MANGA_SOURCE,
                        Contract.MyManga.COLUMN_MANGA_COVER},
                null, null, null);

        if (cursor == null || !cursor.moveToFirst()) return Result.FAILURE;

        do {
            try {
                MangaDetails detailsObject;
                if (cursor.getString(MANGA_SOURCE).equals(context.getString(R.string.pref_manga_reader))) {
                    detailsObject = MangaReader.getInstance(new WeakReference<>(context)).getManga(cursor.getString(MANGA_ID));
                } else {
                    detailsObject = MangaFox.getInstance(new WeakReference<>(context)).getManga(cursor.getString(MANGA_ID));
                }

                if (detailsObject != null) {
                    String last_local = Chapter.fromJSON(new JSONArray(cursor.getString(MANGA_CHAPTER_JSON)))[0].chapterId;
                    String last_fresh = detailsObject.getChapters()[0].chapterId;

                    if (!last_local.equals(last_fresh)) {
                        ContentValues cv = new ContentValues();
                        cv.put(Contract.MyManga.COLUMN_MANGA_CHAPTER_JSON,
                                new Gson().toJson(detailsObject.getChapters()));

                        repository.updateMangaDetails(detailsObject.getName(), cv);

                        //get number of updates if user didn't go through them already
                        int updates = Utilities.readMangaPageFromPrefs(context, cursor.getString(MANGA_ID));
                        updates += (Integer.parseInt(last_fresh.trim()) - Integer.parseInt(last_local.trim()));
                        Utilities.writeMangaPageToPrefs(context, cursor.getString(MANGA_ID), updates);

                        updated.add(detailsObject.getName());
                        Utilities.Log(TAG, detailsObject.getName() + ": updated");
                    } else {
                        Utilities.Log(TAG, detailsObject.getName() + ": No update");
                    }

                    if (!cursor.getString(MANGA_COVER).equals(detailsObject.getCover())) {
                        ContentValues cv = new ContentValues();
                        cv.put(Contract.MyManga.COLUMN_MANGA_COVER, detailsObject.getCover());
                        repository.updateMangaDetails(detailsObject.getName(), cv);
                    }
                } else {
                    Utilities.Log(TAG, "details object null");
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return Result.FAILURE;
            }


        } while (cursor.moveToNext());

        if (!cursor.isClosed()) cursor.close();

        if (updated.size() > 0) notifyUser(updated, context);

        Intent intent = new Intent(INTENT_FILTER);
        intent.putExtra(UPDATE_LIST_EXTRA, updated.toArray());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        Utilities.Log(TAG, "Updates: " + updated.size());

        return Result.SUCCESS;
    }

    private void notifyUser(List<String> list, Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(MainActivity.ACTION_UPDATE);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String bigString = "Updated: ";
        String contextText = list.size() + (list.size() == 1 ? " Manga in your library has been updated." :
                " Manga in your library have been updated.");
        int i = 1;
        for (String s : list) {
            bigString = bigString.concat(s + (i != list.size() ? ", " : "."));
            i++;
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_stat_maps_local_library)
                        .setContentTitle("Manga Updates")
                        .setContentText(contextText)
                        .setNumber(list.size())
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(pendingIntent)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(bigString))
                        .setColor(new MyColorUtils(context).getAccentColor())
                        .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

}

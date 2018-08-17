package com.freddieptf.mangatest.data.sync;

import android.util.Log;

import com.evernote.android.job.JobManager;

/**
 * Created by fred on 5/2/15.
 */
public class SyncHelper {

    private static final String TAG = "SyncHelper";

    public static void scheduleJobs() {
        JobManager manager = JobManager.instance();

        Log.d(TAG, "scheduledJobs: size - " + manager.getAllJobRequests().size());

        if (manager.getAllJobRequestsForTag(MangaLibrarySync.TAG).size() <= 0) {
            MangaLibrarySync.scheduleJob();
        }

        if (manager.getAllJobRequestsForTag(LatestMangaSync.TAG).size() <= 0) {
            LatestMangaSync.scheduleJob();
        }
    }
}

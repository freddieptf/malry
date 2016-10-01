package com.freddieptf.mangatest.data.sync;

/**
 * Created by fred on 5/2/15.
 */
public class SyncHelper {

    public static void scheduleJobs() {
        MangaLibrarySync.scheduleJob();
        LatestMangaSync.scheduleJob();
    }
}

package com.freddieptf.mangatest.data.sync;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by freddieptf on 22/09/16.
 */

public class SyncJobCreator implements JobCreator {

    @Override
    public Job create(String tag) {
        switch (tag) {
            case MangaLibrarySync.TAG:
                return new MangaLibrarySync();
            case LatestMangaSync.TAG:
                return new LatestMangaSync();
            default:
                return null;
        }
    }
}

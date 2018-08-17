package com.freddieptf.mangatest.data.sync;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;

/**
 * Created by freddieptf on 22/09/16.
 */

public class LatestMangaSync extends Job {

    public static final String TAG = "LatestMangaSyncJobTAG";

    public static void scheduleJob() {

    }

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        // TODO: 22/09/16 implement
        return null;
    }
}

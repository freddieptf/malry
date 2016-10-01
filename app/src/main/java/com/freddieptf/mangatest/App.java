package com.freddieptf.mangatest;

import android.app.Application;

import com.evernote.android.job.JobManager;
import com.freddieptf.mangatest.data.sync.SyncJobCreator;

/**
 * Created by freddieptf on 22/09/16.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JobManager.create(this).addJobCreator(new SyncJobCreator());
    }

}

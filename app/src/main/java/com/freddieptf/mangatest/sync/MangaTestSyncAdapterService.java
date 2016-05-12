package com.freddieptf.mangatest.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by fred on 5/2/15.
 */
public class MangaTestSyncAdapterService extends Service {

    private static final Object adpterLock = new Object();
    private static MangaTestSyncAdapter mangaTestSyncAdapter = null;

    @Override
    public void onCreate() {
        super.onCreate();
        android.util.Log.d("SyncAdapterService", "created");

        synchronized (adpterLock){
            if(mangaTestSyncAdapter == null){
                mangaTestSyncAdapter = new MangaTestSyncAdapter(getApplicationContext(), true);
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mangaTestSyncAdapter.getSyncAdapterBinder();
    }
}

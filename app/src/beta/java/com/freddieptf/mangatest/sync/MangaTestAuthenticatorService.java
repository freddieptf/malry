package com.freddieptf.mangatest.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by fred on 5/2/15.
 */
public class MangaTestAuthenticatorService extends Service {

    MangaTestAuthenticator mangaTestAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        mangaTestAuthenticator = new MangaTestAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mangaTestAuthenticator.getIBinder();
    }
}


package com.freddieptf.mangatest.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.freddieptf.mangatest.api.Downloader;
import com.freddieptf.mangatest.beans.NetworkChapterAttrs;
import com.freddieptf.mangatest.utils.Utilities;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by fred on 9/7/15.
 */
public class MangaDownloadServiceTest extends Service {

    public static final String ARRAY_LIST = "array_list";
    String LOG_TAG = getClass().getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utilities.Log(LOG_TAG, "start command");

        final ArrayList<NetworkChapterAttrs> mangaChapterAttrs =
                (ArrayList<NetworkChapterAttrs>) intent.getExtras().get(ARRAY_LIST);

        Random random = new Random();
        Downloader.getInstance().submitDownload(mangaChapterAttrs,  random.nextInt(10000));

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utilities.Log(LOG_TAG, "destroyed");
        Downloader.getInstance().shutdownDownloader(); //should probably change to shutdownNow()
    }

}

package com.freddieptf.mangatest.data.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.model.ChapterPages;
import com.freddieptf.mangatest.data.remote.Downloader;
import com.freddieptf.mangatest.data.remote.MangaFox;
import com.freddieptf.mangatest.data.remote.MangaReader;
import com.freddieptf.mangatest.utils.Utilities;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Random;

/**
 * Created by fred on 9/7/15.
 */
public class ChapterDownloadService extends Service implements Downloader.DownloadsEndCallback {

    public static final String MANGA_NAME = "manga_name";
    public static final String MANGA_CHAPTER = "manga_ch";
    public static final String MANGA_SOURCE = "manga_source";
    public static final int NOTIFICAATION_ID = 344545;
    private static final String TAG = "ChapterDownloadService";
    private Downloader downloader;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downloader = Downloader.getInstance(this);
        downloader.setDownloadEndCallback(this);
        startForeground(NOTIFICAATION_ID, downloader.getForegroundNotification());
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Utilities.Log(TAG, "start command");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String manga = intent.getStringExtra(MANGA_NAME);
                String chId = intent.getStringExtra(MANGA_CHAPTER);
                String source = intent.getStringExtra(MANGA_SOURCE);

                ChapterPages chapterPages;
                try {
                    if (source.equals(getBaseContext().getString(R.string.pref_manga_reader))) {
                        chapterPages = MangaReader.getInstance(new WeakReference<>(getBaseContext()))
                                .getMangaChapterPages(manga, chId);
                    } else {
                        chapterPages = MangaFox.getInstance(new WeakReference<>(getBaseContext()))
                                .getMangaChapterPages(manga, chId);
                    }

                    chapterPages.setMangaName(manga);
                    chapterPages.setChapter(chId);

                    Random random = new Random();
                    int id = random.nextInt(10000);

                    downloader.submitDownload(chapterPages, id);

                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), "Chapter Download failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    stopForeground(true);
                    stopSelf();
                }
            }
        });

        thread.start();
        try {
            // FIXME: 22/09/16 yeaaah..don't do this
            thread.join();
        } catch (InterruptedException e) {
            Toast.makeText(getBaseContext(), "Chapter Download failed! Please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            stopForeground(true);
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onAllDownloadsComplete() {
        stopForeground(false);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        downloader.shutdownDownloader();
    }

}

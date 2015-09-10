package com.freddieptf.mangatest.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.api.Downloader;
import com.freddieptf.mangatest.beans.NetworkChapterAttrs;
import com.freddieptf.mangatest.utils.MyColorUtils;
import com.freddieptf.mangatest.utils.Utilities;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by fred on 9/7/15.
 */
public class MangaDownloadServiceTest extends Service implements Downloader.PublishProgress {

    public static final String ARRAY_LIST = "array_list";
    String LOG_TAG = getClass().getSimpleName();
    Downloader downloader;
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;
    final int queued = new Random().nextInt(100000);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downloader = Downloader.getInstance();
        downloader.setProgressListener(this);
        builder = new NotificationCompat.Builder(this);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onPublishProgress(Downloader.ProgressData progressData) {
        int max = progressData.getMax();
        int progress = progressData.getProgress() + 1;

        builder.setContentTitle(progressData.getMangaName() + " " + progressData.getChapter());
        builder.setContentText("downloading " + progress + "/" + max)
                .setProgress(max, progress, false);
        notificationManager.notify(progressData.getId(), builder.build());
        if(progress == max) notificationManager.cancel(progressData.getId());
    }

    @Override
    public void activeDownloads(int numOfActiveDownloads) {
        Utilities.Log(LOG_TAG, "downloads: " + numOfActiveDownloads);
        if(numOfActiveDownloads > 3) {
            builder.setContentTitle("Added to Queue");
            builder.setContentText((downloader.getJobs() - 3) + " download(s) queued.")
                    .setProgress(0, 0, true);
            builder.setPriority(Notification.PRIORITY_LOW);
            notificationManager.notify(queued, builder.build());
        } else
            notificationManager.cancel(queued);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utilities.Log(LOG_TAG, "start command");

        final ArrayList<NetworkChapterAttrs> mangaChapterAttrs =
                (ArrayList<NetworkChapterAttrs>) intent.getExtras().get(ARRAY_LIST);

        Random random = new Random();
        int id = random.nextInt(100);

        builder.setContentTitle(mangaChapterAttrs.get(0).getName() + " " +
                mangaChapterAttrs.get(0).getChapter()).setSmallIcon(R.drawable.ic_stat_maps_local_library);
        builder.setColor(new MyColorUtils(this).getAccentColor());
        builder.setContentText("connecting...").setProgress(0, 0, true);
        builder.setPriority(Notification.PRIORITY_HIGH);

        downloader.submitDownload(mangaChapterAttrs, id);

        if(downloader.getJobs() <= 3){
            notificationManager.notify(id, builder.build());
        }


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utilities.Log(LOG_TAG, "destroyed");
        downloader.shutdownDownloader();
    }

}

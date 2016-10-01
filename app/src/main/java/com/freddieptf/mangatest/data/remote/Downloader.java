package com.freddieptf.mangatest.data.remote;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.model.ChapterPages;
import com.freddieptf.mangatest.data.service.ChapterDownloadService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by fred on 9/6/15.
 */
public class Downloader implements DownloadTask.PublishProgress {

    private static final String TAG = "Downloader";
    private static Downloader downloader;
    private final ExecutorService executorService;
    private final NotificationCompat.Builder builder;
    private final NotificationManager notificationManager;
    private DownloadsEndCallback callback;
    private int taskCount = 0;
    private int totalProgress = -1;

    /**
     * Downloader handles one chapter download at a time..for now
     **/
    private Downloader(Context context) {
        executorService = Executors.newSingleThreadExecutor();
        builder = new NotificationCompat.Builder(context);
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    }

    public static Downloader getInstance(Context context) {
        if (downloader == null) downloader = new Downloader(context);
        return downloader;
    }

    public Notification getForegroundNotification() {
        builder.setContentTitle("Starting download")
                .setSmallIcon(R.drawable.ic_stat_maps_local_library)
                .setProgress(0, 0, true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setColor(Color.CYAN);
        return builder.build();
    }

    public void setDownloadEndCallback(DownloadsEndCallback callback) {
        this.callback = callback;
    }

    public void submitDownload(ChapterPages chapterPages, int id) {
        DownloadTask downloadTask = new DownloadTask(chapterPages, id);
        downloadTask.setProgressListner(this);
        executorService.submit(downloadTask);
        taskCount++;
    }

    @Override
    public void onStart(int taskId, int totalPages, String manga, String chapterId) {
        Log.d(TAG, "onStart: " + manga + " " + chapterId);
        builder.setContentTitle(manga + " Ch: " + chapterId)
                .setProgress(totalPages, 0, false);
        notificationManager.notify(ChapterDownloadService.NOTIFICAATION_ID, builder.build());
        totalProgress = totalPages;
    }

    @Override
    public void onProgressUpdate(int taskId, int progress) {
        Log.d(TAG, "onProgressUpdate: " + progress);
        builder.setProgress(totalProgress, progress, false);
        notificationManager.notify(ChapterDownloadService.NOTIFICAATION_ID, builder.build());
    }

    @Override
    public void onComplete(int taskId, String manga, String chapterId) {
        Log.d(TAG, "onComplete: " + manga + " " + chapterId);
        builder.setProgress(0, 0, false)
                .setContentTitle("Download Complete")
                .setContentText(manga + ": " + chapterId + " has been successfully downloaded");
        notificationManager.notify(ChapterDownloadService.NOTIFICAATION_ID, builder.build());

        taskCount--;

        if (taskCount == 0) {
            callback.onAllDownloadsComplete();
        }
    }

    @Override
    public void onError(int taskId, String pageId, Exception e) {
        Log.d(TAG, "onError: " + pageId + " " + e.getMessage());
    }

    public void shutdownDownloader() {
        if (!executorService.isShutdown()) executorService.shutdownNow();
    }

    public interface DownloadsEndCallback {
        void onAllDownloadsComplete();
    }

}
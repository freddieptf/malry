package com.freddieptf.mangatest.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.freddieptf.mangatest.api.Downloader;
import com.freddieptf.mangatest.beans.NetworkChapterAttrs;
import com.freddieptf.mangatest.utils.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        Utilities.Log(LOG_TAG,
                "id: " + progressData.getId() + " "
                        + progressData.getMangaName() + " " + progressData.getChapter()
                        + " " + progressData.getProgress() + "/" + progressData.getMax());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utilities.Log(LOG_TAG, "start command");

        final ArrayList<NetworkChapterAttrs> mangaChapterAttrs =
                (ArrayList<NetworkChapterAttrs>) intent.getExtras().get(ARRAY_LIST);

        Random random = new Random();
        int id = random.nextInt(100);

//        builder.setContentTitle(mangaChapterAttrs.get(0).getName() + " " +
//                mangaChapterAttrs.get(0).getChapter()).setSmallIcon(R.drawable.ic_stat_maps_local_library);
//        builder.setContentText("connecting...").setProgress(0, 0, true);
//        notificationManager.notify(id, builder.build());

        downloader.submitDownload(mangaChapterAttrs, id);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utilities.Log(LOG_TAG, "destroyed");
    }

    public class DownloadImages implements Runnable {
        ArrayList<NetworkChapterAttrs> mangaChapterAttrs;
        int notificationId;
        File parent;

        public DownloadImages (ArrayList<NetworkChapterAttrs> n, int id){
            mangaChapterAttrs = n;
            notificationId = id;
        }

        @Override
        public void run() {
            String parentDirectory = Environment.getExternalStorageDirectory().toString();
            String manga = mangaChapterAttrs.get(0).getName();

            String chapter = mangaChapterAttrs.get(0).getChapter();
            String chapterTitle = mangaChapterAttrs.get(0).getChapterTitle();

            if (Utilities.externalStorageMounted()) {

                parent = new File(parentDirectory + "/MangaTest/" + manga + "/" + chapter + ": " + chapterTitle);
                ArrayList<ImageData> imageDataArrayList = new ArrayList<>();

                if (!parent.exists() || parent.listFiles().length != mangaChapterAttrs.size() - 1) {
                    parent.mkdirs();

                    for (int i = 1; i < mangaChapterAttrs.size(); i++) {
                        ImageData imageData = new ImageData();
                        imageData.setImageUrl(mangaChapterAttrs.get(i).getImageUrl());
                        imageData.setPageId(mangaChapterAttrs.get(i).getPageId());
                        imageDataArrayList.add(imageData);
                    }

                    Log.d(LOG_TAG + " " + notificationId, "ImageDatas Size: " + imageDataArrayList.size());

                    downloadStuff(imageDataArrayList);

                } else{
                    Utilities.Log(LOG_TAG, "Already there!");
                }

            }
        }


        final void downloadStuff(ArrayList<ImageData> imageDatas) {
            for(int i = 0; i < imageDatas.size(); i++){
                Utilities.Log(LOG_TAG + " " + notificationId, "downloading: " + imageDatas.get(i).getImageUrl());
                Bitmap bitmap = Utilities.DownloadBitmapFromUrl(imageDatas.get(i).getImageUrl());
                String pageId = imageDatas.get(i).getPageId();
                File file = new File(parent, pageId + ".jpg");

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG + " ImageRequest: ", e.toString());
                }

            }
        }
    }

    private class ImageData{
        String imageUrl, pageId;

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getPageId() {
            return pageId;
        }

        public void setPageId(String pageId) {
            this.pageId = pageId;
        }

    }

}

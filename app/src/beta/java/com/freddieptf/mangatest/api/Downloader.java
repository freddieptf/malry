package com.freddieptf.mangatest.api;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.freddieptf.mangatest.beans.NetworkChapterAttrs;
import com.freddieptf.mangatest.utils.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by fred on 9/6/15.
 */
public class Downloader {

    final String LOG_TAG = getClass().getSimpleName();
    ExecutorService executorService;
    int i = 0;

    private static Downloader downloader;

    public static Downloader getInstance(){
        if(downloader == null) downloader = new Downloader();
        return downloader;
    }

    PublishProgress publishProgress;

    public Downloader() {
        executorService = Executors.newFixedThreadPool(3);
    }

    public void setProgressListener(PublishProgress publishProgress){
        this.publishProgress = publishProgress;
    }

    public int getJobs(){
        return i;
    }

    public void submitDownload(ArrayList<NetworkChapterAttrs> n, int id){
        executorService.submit(new DownloadImages(n, id));
        i++;
        publishProgress.activeDownloads(i);
    }

    public void shutdownDownloader(){
        if(!executorService.isShutdown()) executorService.shutdownNow();
    }

    public class DownloadImages implements Runnable {
        ArrayList<NetworkChapterAttrs> mangaChapterAttrs;
        int notificationId;
        File parent;
        ProgressData progressData;

        public DownloadImages (ArrayList<NetworkChapterAttrs> n, int id){
            mangaChapterAttrs = n;
            notificationId = id;
            progressData = new ProgressData();
        }

        @Override
        public void run() {
            String parentDirectory = Environment.getExternalStorageDirectory().toString();
            String manga = mangaChapterAttrs.get(0).getName();
            progressData.setMangaName(manga);
            progressData.setId(notificationId);

            String chapter = mangaChapterAttrs.get(0).getChapter();
            progressData.setChapter(chapter);
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
                    i--;
                    publishProgress.activeDownloads(i);

                } else{
                    Utilities.Log(LOG_TAG, "Already there!");
                }

            }
        }


        void downloadStuff(ArrayList<ImageData> imageDatas) {
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

                    progressData.setMax(imageDatas.size());
                    progressData.setProgress(i);
                    publishProgress.onPublishProgress(progressData);
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

    public interface PublishProgress{
        void onPublishProgress(ProgressData progressData);
        void activeDownloads(int numOfActiveDownloads);
    }

    public class ProgressData{
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public String getMangaName() {
            return mangaName;
        }

        public void setMangaName(String mangaName) {
            this.mangaName = mangaName;
        }

        public String getChapter() {
            return chapter;
        }

        public void setChapter(String chapter) {
            this.chapter = chapter;
        }

        int id;
        int progress;
        int max;
        String mangaName, chapter;
    }
}

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

    private static Downloader downloader;

    public static Downloader getInstance(){
        if(downloader == null) downloader = new Downloader();
        return downloader;
    }

    public Downloader() {
        executorService = Executors.newFixedThreadPool(3);
    }


    public void submitDownload(ArrayList<NetworkChapterAttrs> n, int id){
        executorService.submit(new DownloadImages(n, id));
    }

    public void shutdownDownloader(){
        if(!executorService.isShutdown()) executorService.shutdown();
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

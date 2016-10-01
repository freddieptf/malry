package com.freddieptf.mangatest.data.remote;

import android.graphics.Bitmap;
import android.os.Environment;

import com.freddieptf.mangatest.data.model.ChapterPages;
import com.freddieptf.mangatest.data.model.ImagePage;
import com.freddieptf.mangatest.utils.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by freddieptf on 22/09/16.
 */

class DownloadTask implements Runnable {

    private final ChapterPages chapterImages;
    private final int taskId;
    private int currentProgress = 0;
    private File parent;
    private PublishProgress publishProgress;

    DownloadTask(ChapterPages chapterPages, int taskId) {
        this.chapterImages = chapterPages;
        this.taskId = taskId;
    }

    void setProgressListner(PublishProgress publishProgress) {
        this.publishProgress = publishProgress;
    }

    @Override
    public void run() {
        String parentDirectory = Environment.getExternalStorageDirectory().toString();
        String manga = chapterImages.getMangaName();

        String chapter = chapterImages.getChapter();
        String chapterTitle = chapterImages.getChapterTitle();

        if (Utilities.externalStorageMounted()) {
            parent = new File(parentDirectory + "/MangaTest/" + manga + "/" + chapter + ": " + chapterTitle);
            if (!parent.exists() || parent.listFiles().length != chapterImages.getImagePages().length - 1) {
                parent.mkdirs();

                publishProgress.onStart(taskId, chapterImages.getImagePages().length, manga, chapter);

                for (ImagePage imagePage : chapterImages.getImagePages()) {
                    downloadImagePage(imagePage);
                    currentProgress++;
                }

                publishProgress.onComplete(taskId, manga, chapter);
            }
        }
    }

    private void downloadImagePage(ImagePage imagePage) {
        Bitmap bitmap = Utilities.DownloadBitmapFromUrl(imagePage.getUrl());
        String pageId = imagePage.getPageId();
        File file = new File(parent, pageId + ".jpg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file.getPath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);
            fileOutputStream.close();
            publishProgress.onProgressUpdate(taskId, currentProgress);
        } catch (IOException e) {
            e.printStackTrace();
            publishProgress.onError(taskId, pageId, e);
        }

    }

    interface PublishProgress {
        void onStart(int taskId, int totalPages, String manga, String chapterId);

        void onProgressUpdate(int taskId, int progress);

        void onComplete(int taskId, String manga, String chapterId);

        void onError(int taskId, String pageId, Exception e);
    }

}


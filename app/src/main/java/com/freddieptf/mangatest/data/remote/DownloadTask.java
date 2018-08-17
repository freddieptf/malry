package com.freddieptf.mangatest.data.remote;

import android.graphics.Bitmap;

import com.freddieptf.mangatest.data.model.ChapterPages;
import com.freddieptf.mangatest.data.model.ImagePage;
import com.freddieptf.mangatest.utils.FileUtils;
import com.freddieptf.mangatest.utils.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

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
        String manga = chapterImages.getMangaName().trim();
        String chapter = chapterImages.getChapter().trim();
        String chapterTitle = chapterImages.getChapterTitle().trim();

        if (Utilities.externalStorageMounted()) {
            parent = new File(FileUtils.getMangaChapterDir(manga, chapter + ": " + chapterTitle));
            if (!parent.exists() || parent.listFiles().length != chapterImages.getImagePages().length - 1) {
                parent.mkdirs();

                publishProgress.onStart(taskId, chapterImages.getImagePages().length, manga, chapter);

                ImagePage[] pages = chapterImages.getImagePages();
                Arrays.sort(pages, new Comparator<ImagePage>() {
                    @Override
                    public int compare(ImagePage imagePage, ImagePage t1) {
                        return t1.getPageId() < imagePage.getPageId() ? 1 : -1;
                    }
                });
                for (ImagePage imagePage : pages) {
                    downloadImagePage(imagePage);
                    currentProgress++;
                }

                publishProgress.onComplete(taskId, manga, chapter);
            }
        }
    }

    private void downloadImagePage(ImagePage imagePage) {
        Bitmap bitmap = Utilities.DownloadBitmapFromUrl(imagePage.getUrl());
        int pageId = imagePage.getPageId();
        File file = new File(parent, pageId + ".jpg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file.getPath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);
            fileOutputStream.close();
            publishProgress.onProgressUpdate(taskId, currentProgress);
        } catch (IOException e) {
            e.printStackTrace();
            publishProgress.onError(taskId, pageId + "", e);
        }

    }

    interface PublishProgress {
        void onStart(int taskId, int totalPages, String manga, String chapterId);

        void onProgressUpdate(int taskId, int progress);

        void onComplete(int taskId, String manga, String chapterId);

        void onError(int taskId, String pageId, Exception e);
    }

}


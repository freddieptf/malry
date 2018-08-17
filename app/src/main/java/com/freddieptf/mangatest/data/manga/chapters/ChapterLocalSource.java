package com.freddieptf.mangatest.data.manga.chapters;

import android.content.Context;

import com.freddieptf.mangatest.data.model.Chapter;
import com.freddieptf.mangatest.data.model.ChapterPages;
import com.freddieptf.mangatest.data.model.ImagePage;
import com.freddieptf.mangatest.utils.FileUtils;

import java.io.File;

/**
 * Created by freddieptf on 18/10/16.
 */

public class ChapterLocalSource implements ChapterDataSource {

    private static final String TAG = "ChapterLocalSource";

    @Override
    public ChapterPages getChapter(String source, String manga_id, Chapter chapter, Context context) {
        File file = new File(FileUtils.getMangaChapterDir(manga_id, chapter.chapterId + ": " + chapter.chapterTitle));
        if (file.exists()) {
            File[] files = file.listFiles();
            ImagePage[] pages = new ImagePage[files.length];
            int i = 0;
            for (File f : files) {
                pages[i] = new ImagePage(i, f.getAbsolutePath());
                i++;
            }
            return new ChapterPages(manga_id, chapter.chapterId, chapter.chapterTitle, pages);
        }
        return null;
    }
}

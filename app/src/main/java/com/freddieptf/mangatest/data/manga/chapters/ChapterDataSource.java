package com.freddieptf.mangatest.data.manga.chapters;

import android.content.Context;

import com.freddieptf.mangatest.data.model.Chapter;
import com.freddieptf.mangatest.data.model.ChapterPages;

/**
 * Created by freddieptf on 18/10/16.
 */

public interface ChapterDataSource {
    ChapterPages getChapter(String source, String manga_id, Chapter chapter, Context context);
}

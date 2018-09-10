package com.freddieptf.reader.data

import com.freddieptf.reader.data.dao.ChapterCacheDao

/**
 * Created by freddieptf on 9/9/18.
 */

interface ReaderDB {
    fun chapterCacheDao(): ChapterCacheDao
}

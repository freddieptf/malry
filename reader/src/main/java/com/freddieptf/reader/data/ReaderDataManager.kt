package com.freddieptf.reader.data

import com.freddieptf.reader.data.models.ChapterCache

/**
 * Created by freddieptf on 9/9/18.
 */
object ReaderDataManager {

    private lateinit var readerDB: ReaderDB

    fun use(readerDB: ReaderDB) {
        this.readerDB = readerDB
    }

    fun getCache(id: String): ChapterCache {
        return readerDB.chapterCacheDao().get(id)
    }

    fun save(chapter: ChapterCache) {
        readerDB.chapterCacheDao().save(chapter)
    }

}
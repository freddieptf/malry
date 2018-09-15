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

    fun getCache(parent: String, chapter: String): ChapterCache {
        return readerDB.chapterCacheDao().get(parent+"/"+chapter)
    }

    fun isChapterRead(parent: String, chapter: String): Boolean {
        val cache = getCache(parent, chapter)
        return cache != null && cache.totalPages - 1 == cache.page
    }

    fun save(chapter: ChapterCache) {
        readerDB.chapterCacheDao().save(chapter)
    }

}
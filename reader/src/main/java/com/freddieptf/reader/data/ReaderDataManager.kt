package com.freddieptf.reader.data

import androidx.collection.SimpleArrayMap
import androidx.lifecycle.LiveData
import com.freddieptf.reader.data.models.ChReadCache

/**
 * Created by freddieptf on 9/9/18.
 */
object ReaderDataManager {

    private val TAG = ReaderDataManager::class.java.simpleName
    private lateinit var readerDB: ReaderDB
    private val cacheMap = SimpleArrayMap<String, LiveData<ChReadCache>>()

    fun use(readerDB: ReaderDB) {
        this.readerDB = readerDB
    }

    fun getCache(): LiveData<List<ChReadCache>> {
        return readerDB.chapterCacheDao().get()
    }

    fun getCache(parent: String, chapter: String): ChReadCache? {
        return readerDB.chapterCacheDao().get(parent+"/"+chapter)
    }

    fun isChapterRead(parent: String, chapter: String): Boolean {
        val cache = getCache(parent, chapter)
        return cache != null && cache.totalPages - 1 == cache.page
    }

    fun save(chapter: ChReadCache) {
        readerDB.chapterCacheDao().save(chapter)
    }

}
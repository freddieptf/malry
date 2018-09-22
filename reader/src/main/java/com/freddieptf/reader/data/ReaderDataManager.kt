package com.freddieptf.reader.data

import androidx.collection.SimpleArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.freddieptf.reader.data.models.ChapterCache

/**
 * Created by freddieptf on 9/9/18.
 */
object ReaderDataManager {

    private val TAG = ReaderDataManager::class.java.simpleName
    private lateinit var readerDB: ReaderDB
    private val cacheMap = SimpleArrayMap<String, LiveData<ChapterCache>>()
    private val bcast = MutableLiveData<ChapterCache>()

    fun use(readerDB: ReaderDB) {
        this.readerDB = readerDB
    }

    private fun getCache(parent: String, chapter: String): ChapterCache {
        return readerDB.chapterCacheDao().get(parent+"/"+chapter)
    }

    fun getLCacheBCast(): LiveData<ChapterCache> {
        return bcast
    }

    fun getLCache(parent: String, chapter: String): LiveData<ChapterCache> {
        var ld = cacheMap.get(parent+"/"+chapter)
        if (ld == null) ld = readerDB.chapterCacheDao().getL(parent+"/"+chapter)
        ld.observeForever {
            bcast.value = it
        }
        return ld
    }

    fun isChapterRead(parent: String, chapter: String): Boolean {
        val cache = getCache(parent, chapter)
        return cache != null && cache.totalPages - 1 == cache.page
    }

    fun save(chapter: ChapterCache) {
        readerDB.chapterCacheDao().save(chapter)
    }

}
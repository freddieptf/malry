package com.freddieptf.malry.data

import androidx.lifecycle.LiveData
import com.freddieptf.malry.data.db.LibraryDB
import com.freddieptf.malry.data.db.models.Chapter
import com.freddieptf.malry.data.db.models.LibraryItem

/**
 * Created by freddieptf on 9/17/18.
 */
class DbDataSource(private val db: LibraryDB) {

    internal fun getLibraryItems(): LiveData<List<com.freddieptf.malry.api.LibraryItem>> {
        return db.LibraryItemDao().getLibraryItems()
    }

    internal fun getLibraryItem(ID: String): com.freddieptf.malry.api.LibraryItem? {
        return db.LibraryItemDao().getLibraryItem(ID)
    }

    internal fun saveLibraryItem(item: LibraryItem) {
        return db.LibraryItemDao().saveLibraryItem(item)
    }

    internal fun saveLibraryItems(items: List<com.freddieptf.malry.api.LibraryItem>) {
        return db.LibraryItemDao().saveLibraryItems(items.map {
            LibraryItem(
                    ID = it.ID,
                    dirUri = it.dirURI!!,
                    sourceID = it.sourceID,
                    title = it.title,
                    itemCount = it.itemCount,
                    coverImg = it.coverImg
            )
        })
    }

    fun getChapters(mangaID: String): List<com.freddieptf.malry.api.Chapter> {
        return db.ChapterDao().getChapters(mangaID)
    }

    internal fun getChaptersLive(mangaID: String): LiveData<List<com.freddieptf.malry.api.Chapter>> {
        return db.ChapterDao().getChaptersLiveData(mangaID)
    }

    internal fun saveChapters(data: List<Chapter>) {
        if (data.isEmpty()) return
        db.ChapterDao().saveChapters(data)
    }

    fun getChapter(chapterID: String): com.freddieptf.malry.api.Chapter {
        return db.ChapterDao().getChapter(chapterID)
    }

    fun getLastRead(libraryItemID: String): com.freddieptf.malry.api.Chapter? {
        return db.ChapterDao().getLastReadChapter(libraryItemID)
    }

    fun setLastReadPage(chapterID: String, page: Int, totalPages: Int) {
        db.ChapterDao().setLastReadPage(chapterID, page, totalPages, System.currentTimeMillis())
    }

}
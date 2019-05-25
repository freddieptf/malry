package com.freddieptf.malry.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
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

    internal fun saveLibraryItems(items: List<LibraryItem>) {
        return db.LibraryItemDao().saveLibraryItems(items)
    }

    fun getChapters(mangaID: String): List<com.freddieptf.malry.api.Chapter> {
        return db.ChapterDao().getChapters(mangaID)
                .map { c ->
                    com.freddieptf.malry.api.Chapter(c.ID, c.docID, c.name, c.parentID, null).apply {
                        lastReadPage = c.lastReadPage
                        totalPages = c.totalPages
                    }
                }
    }

    internal fun getChaptersLive(mangaID: String): LiveData<List<com.freddieptf.malry.api.Chapter>> {
        return Transformations.map(db.ChapterDao().getChaptersLiveData(mangaID))
        { items ->
            items.map { c ->
                com.freddieptf.malry.api.Chapter(c.ID, c.docID, c.name, c.parentID, null).apply {
                    lastReadPage = c.lastReadPage
                    totalPages = c.totalPages
                }
            }
        }
    }


    internal fun saveChapters(data: List<Chapter>) {
        if (data.isEmpty()) return
        db.ChapterDao().saveChapters(data)
    }

    fun getChapter(chapterID: String): com.freddieptf.malry.api.Chapter {
        val c = db.ChapterDao().getChapter(chapterID)
        return com.freddieptf.malry.api.Chapter(c.ID, c.docID, c.name, c.parentID, null).apply {
            lastReadPage = c.lastReadPage
            totalPages = c.totalPages
        }
    }

    fun getLastRead(libraryItemID: String): com.freddieptf.malry.api.Chapter? {
        val c = db.ChapterDao().getLastReadChapter(libraryItemID)
        return when (c) {
            null -> null
            else -> com.freddieptf.malry.api.Chapter(c.ID, c.docID, c.name, c.parentID, null).apply {
                lastReadPage = c.lastReadPage
                totalPages = c.totalPages
            }
        }
    }

    fun setLastReadPage(chapterID: String, page: Int, totalPages: Int) {
        db.ChapterDao().setLastReadPage(chapterID, page, totalPages, System.currentTimeMillis())
    }

}
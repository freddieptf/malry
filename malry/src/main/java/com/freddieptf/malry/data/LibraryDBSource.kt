package com.freddieptf.malry.data

import android.net.Uri
import com.freddieptf.malry.data.models.Chapter
import com.freddieptf.malry.data.models.LibraryItem

/**
 * Created by freddieptf on 9/17/18.
 */
internal object LibraryDBSource {

    private lateinit var db: LibraryDB

    fun use(db: LibraryDB) {
        LibraryDBSource.db = db
    }

    fun getLibraryItems(): List<LibraryItem> {
        return db.LibraryItemDao().getLibraryItems()
    }

    fun saveLibraryItem(item: LibraryItem) {
        return db.LibraryItemDao().saveLibraryItem(item)
    }

    fun getChapters(mangaDirUri: Uri): List<com.freddieptf.malry.api.Chapter> {
        return db.ChapterDao().getChapters(mangaDirUri)
                .map { c ->
                    com.freddieptf.malry.api.Chapter(c.docID, c.name, c.parentUri.toString(), c.parentName).apply {
                        lastReadPage = c.lastReadPage
                        totalPages = c.totalPages
                    }
                }
    }


    fun saveChapters(data: List<Chapter>) {
        if (data.isEmpty()) return
        db.ChapterDao().saveChapters(data)
    }

    fun getChapter(chapterID: String): com.freddieptf.malry.api.Chapter {
        val c = db.ChapterDao().getChapter(chapterID)
        return com.freddieptf.malry.api.Chapter(c.docID, c.name, c.parentUri.toString(), c.parentName).apply {
            lastReadPage = c.lastReadPage
            totalPages = c.totalPages
        }
    }

    fun getLastRead(libraryItemID: String): com.freddieptf.malry.api.Chapter? {
        val c = db.ChapterDao().getLastReadChapter(libraryItemID)
        return when (c) {
            null -> null
            else -> com.freddieptf.malry.api.Chapter(c.docID, c.name, c.parentUri.toString(), c.parentName).apply {
                lastReadPage = c.lastReadPage
                totalPages = c.totalPages
            }
        }
    }

    fun setLastReadPage(chapterID: String, page: Int, totalPages: Int) {
        db.ChapterDao().setLastReadPage(chapterID, page, totalPages, System.currentTimeMillis())
    }

}
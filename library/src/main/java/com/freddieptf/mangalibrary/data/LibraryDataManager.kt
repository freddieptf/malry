package com.freddieptf.mangalibrary.data

import android.net.Uri
import androidx.lifecycle.LiveData
import com.freddieptf.mangalibrary.data.models.Chapter
import com.freddieptf.mangalibrary.data.models.LibraryItem

/**
 * Created by freddieptf on 9/17/18.
 */
object LibraryDataManager {

    private lateinit var db: LibraryDB

    fun use(db: LibraryDB) {
        this.db = db
    }

    fun getLibraryItems(): LiveData<List<LibraryItem>> {
        return db.LibraryItemDao().getLibraryItems()
    }

    fun saveLibraryItem(item: LibraryItem) {
        return db.LibraryItemDao().saveLibraryItem(item)
    }

    fun getChapters(mangaDirUri: Uri): LiveData<List<Chapter>> {
        return db.LibraryItemDao().getChapters(mangaDirUri)
    }

    fun saveChapters(data: List<Chapter>) {
        return db.LibraryItemDao().saveChapters(data)
    }

}
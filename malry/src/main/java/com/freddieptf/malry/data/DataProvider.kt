package com.freddieptf.malry.data

import android.net.Uri
import androidx.lifecycle.LiveData
import com.freddieptf.malry.api.Chapter
import com.freddieptf.malry.api.ChapterProvider
import com.freddieptf.malry.api.LibraryItem

/**
 * Created by freddieptf on 11/14/18.
 */
class DataProvider(private val localDbSource: DbDataSource,
                   private val storageDataSource: StorageDataSource) {

    suspend fun saveToLibrary(libLocation: Uri) {
        val items = storageDataSource.getLibraryItems(libLocation)
        localDbSource.saveLibraryItems(items)
    }

    fun getLibraryItemFromLibrary(ID: String): LibraryItem? {
        return localDbSource.getLibraryItem(ID)
    }

    fun getLibraryItems(): LiveData<List<com.freddieptf.malry.api.LibraryItem>> {
        return localDbSource.getLibraryItems()
    }

    suspend fun saveChapters(dirUri: Uri) {
        localDbSource.saveChapters(storageDataSource.getChapters(dirUri))
    }

    suspend fun saveChapters(chapters: List<Chapter>) {
        localDbSource.saveChapters(chapters.map {
            com.freddieptf.malry.data.db.models.Chapter(it.id, it.docID, it.title, "", it.parentID)
        })
    }

    fun getChapters(libraryItemID: String): LiveData<List<com.freddieptf.malry.api.Chapter>> {
        return localDbSource.getChaptersLive(libraryItemID)
    }

    fun getLastRead(libraryItem: com.freddieptf.malry.api.LibraryItem): com.freddieptf.malry.api.Chapter? {
        return localDbSource.getLastRead(libraryItem.ID)
    }

    fun getChapterProvider(chapter: com.freddieptf.malry.api.Chapter): ChapterProvider {
        return ChapterProvider(chapter, localDbSource)
    }

}
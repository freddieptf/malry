package com.freddieptf.malry.data

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.freddieptf.malry.api.ChapterProvider

/**
 * Created by freddieptf on 11/14/18.
 */
class DataProvider(private val localDbSource: DbDataSource,
                   private val storageDataSource: StorageDataSource) {

    suspend fun saveToLibrary(libLocation: Uri) {
        val items = storageDataSource.getLibraryItems(libLocation)
        localDbSource.saveLibraryItems(items)
    }

    fun getLibraryItems(): LiveData<List<com.freddieptf.malry.api.LibraryItem>> {
        return Transformations.map(localDbSource.getLibraryItems())
        {
            it.map {
                com.freddieptf.malry.api.LibraryItem(it.dirUri, it.name, "").apply {
                    itemCount = it.itemCount
                }
            }
        }
    }

    suspend fun saveLibraryItemChildren(libraryItemUri: Uri) {
        localDbSource.saveChapters(storageDataSource.getChapters(libraryItemUri))
    }

    fun getLibraryItemChildren(libraryItemUri: Uri): LiveData<List<com.freddieptf.malry.api.Chapter>> {
        return localDbSource.getChaptersLive(libraryItemUri)
    }

    fun getLastRead(libraryItem: com.freddieptf.malry.api.LibraryItem): com.freddieptf.malry.api.Chapter? {
        return localDbSource.getLastRead(libraryItem.uri.toString())
    }

    fun getChapterProvider(chapter: com.freddieptf.malry.api.Chapter): ChapterProvider {
        return ChapterProvider(chapter, localDbSource)
    }

}
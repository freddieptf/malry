package com.freddieptf.malry

import android.net.Uri
import androidx.lifecycle.LiveData
import com.freddieptf.malry.api.Chapter
import com.freddieptf.malry.api.ChapterProvider
import com.freddieptf.malry.api.DataProvider
import com.freddieptf.malry.api.LibraryItem
import javax.inject.Singleton

/**
 * Created by freddieptf on 11/17/18.
 */
@Singleton
class ProviderManager constructor(private val localProvider: DataProvider) : DataProvider() {


    override fun getChapterProvider(chapter: Chapter): ChapterProvider {
        return localProvider.getChapterProvider(chapter)
    }

    override fun getLibraryItems(): LiveData<List<LibraryItem>> {
        return localProvider.getLibraryItems()
    }

    override fun openLibraryItem(libraryItemUri: Uri): LiveData<List<Chapter>> {
        return localProvider.openLibraryItem(libraryItemUri)
    }

    override fun getLastRead(libraryItem: LibraryItem): Chapter? {
        return localProvider.getLastRead(libraryItem)
    }

}
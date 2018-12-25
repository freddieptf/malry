package com.freddieptf.malry.api

import android.net.Uri
import androidx.lifecycle.LiveData

/**
 * Created by freddieptf on 11/17/18.
 *
 * We might not need this...i don't see us using any other
 * "first party" provider other than the storage one
 *
 * Premature abstraction? just maybe...
 */
abstract class DataProvider {

    abstract fun getLibraryItems(): LiveData<List<LibraryItem>>

    abstract fun openLibraryItem(libraryItemUri: Uri): LiveData<List<Chapter>>

    abstract fun getLastRead(libraryItem: LibraryItem): Chapter?

    abstract fun getChapterProvider(chapter: Chapter): ChapterProvider

}
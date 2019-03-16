package com.freddieptf.malry.api

import android.net.Uri

/**
 * Created by freddieptf on 11/17/18.
 */
abstract class DataProvider {

    abstract fun importLibrary(libLocation: Uri)

    abstract suspend fun getLibraryItems(): List<LibraryItem>

    abstract suspend fun importLibraryItemChildren(libraryItemUri: Uri)

    abstract suspend fun getLibraryItemChildren(libraryItemUri: Uri): List<Chapter>

    abstract fun getLastRead(libraryItem: LibraryItem): Chapter?

    abstract fun getChapterProvider(chapter: Chapter): ChapterProvider

}
package com.freddieptf.malry.data

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.LiveData
import com.freddieptf.malry.api.ChapterProvider
import com.freddieptf.malry.data.db.models.Chapter
import com.freddieptf.malry.data.db.models.LibraryItem
import com.freddieptf.malry.data.utils.ChapterTitleComparator
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by freddieptf on 11/14/18.
 */
class DataProvider(val ctx: Context, val localDbSource: DbDataSource) {

    fun saveToLibrary(libLocation: Uri) {
        genLibDirs(ctx, libLocation)
    }

    fun getLibraryItems(): List<com.freddieptf.malry.api.LibraryItem> {
        return localDbSource.getLibraryItems().map {
            com.freddieptf.malry.api.LibraryItem(it.dirUri, it.name, "").apply {
                itemCount = it.itemCount
            }
        }
    }

    fun saveLibraryItemChildren(libraryItemUri: Uri) {
        localDbSource.saveChapters(openMangaDir(ctx, libraryItemUri) ?: emptyList())
    }

    fun getLibraryItemChildren(libraryItemUri: Uri): LiveData<List<com.freddieptf.malry.api.Chapter>> {
        return localDbSource.getChaptersLive(libraryItemUri)
    }

    fun getLastRead(libraryItem: com.freddieptf.malry.api.LibraryItem): com.freddieptf.malry.api.Chapter? {
        return localDbSource.getLastRead(libraryItem.uri.toString())
    }

    fun getChapterProvider(chapter: com.freddieptf.malry.api.Chapter): ChapterProvider {
        return ChapterProvider(localDbSource).apply {
            setCurrentRead(chapter)
        }
    }

    private fun genLibDirs(ctx: Context, treeUri: Uri) {
        val libraryDocFile = DocumentFile.fromTreeUri(ctx, treeUri)
        libraryDocFile!!.listFiles().asList().forEach {
            localDbSource.saveLibraryItem(LibraryItem(it.uri, it.name!!, it.listFiles()!!.size, null))
        }
    }

    private fun openMangaDir(ctx: Context, dirUri: Uri): List<Chapter>? {

        val manga = Uri.parse(dirUri.path.replace(":", "/")).lastPathSegment

        val uri = DocumentsContract.buildChildDocumentsUriUsingTree(
                dirUri,
                DocumentsContract.getDocumentId(dirUri)
        )

        val PROJECTION = arrayOf<String>(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE
        )

        val cursor = ctx.contentResolver.query(uri,
                PROJECTION,
                null,
                null,
                null)

        if (!cursor.moveToFirst()) return null

        val chapters = ArrayList<Chapter>()
        do {
            val chapter = Chapter(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    manga,
                    dirUri)
            chapters.add(chapter)
        } while (cursor.moveToNext())

        Collections.sort(chapters, ChapterTitleComparator())
        return chapters
    }
}
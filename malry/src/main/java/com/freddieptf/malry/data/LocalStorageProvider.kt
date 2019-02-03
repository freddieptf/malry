package com.freddieptf.malry.data

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.freddieptf.malry.api.ChapterProvider
import com.freddieptf.malry.api.DataProvider
import com.freddieptf.malry.data.models.Chapter
import com.freddieptf.malry.data.models.LibraryItem
import com.freddieptf.malry.data.utils.ChapterTitleComparator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by freddieptf on 11/14/18.
 */
class LocalStorageProvider(val ctx: Context, db: LibraryDB, var libLocation: Uri) : DataProvider() {

    init {
        LibraryDataManager.use(db)
    }

    override fun openLibraryItem(libraryItemUri: Uri): LiveData<List<com.freddieptf.malry.api.Chapter>> {
        GlobalScope.launch {
            LibraryDataManager.saveChapters(openMangaDir(ctx, libraryItemUri) ?: emptyList())
        }
        return LibraryDataManager.getChaptersLiveData(libraryItemUri)
    }

    override fun getLastRead(libraryItem: com.freddieptf.malry.api.LibraryItem): com.freddieptf.malry.api.Chapter? {
        return LibraryDataManager.getLastRead(libraryItem.uri.toString())
    }

    override fun getLibraryItems(): LiveData<List<com.freddieptf.malry.api.LibraryItem>> {
        GlobalScope.launch {
            genLibDirs(ctx, libLocation)
        }
        return Transformations.map(LibraryDataManager.getLibraryItems()) { list ->
            list.map { it ->
                com.freddieptf.malry.api.LibraryItem(it.dirUri, it.name, "").apply {
                    itemCount = it.itemCount
                }
            }
        }
    }

    override fun getChapterProvider(chapter: com.freddieptf.malry.api.Chapter): ChapterProvider {
        return ChapterProvider(LibraryDataManager).apply {
            setCurrentRead(chapter)
        }
    }

    private fun genLibDirs(ctx: Context, treeUri: Uri) {
        val libraryDocFile = DocumentFile.fromTreeUri(ctx, treeUri)
        libraryDocFile!!.listFiles().asList().forEach {
            LibraryDataManager.saveLibraryItem(LibraryItem(it.uri, it.name!!, it.listFiles()!!.size, null))
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
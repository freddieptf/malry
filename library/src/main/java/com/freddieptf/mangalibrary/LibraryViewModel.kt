package com.freddieptf.mangalibrary

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freddieptf.mangalibrary.data.LibraryDataManager
import com.freddieptf.mangalibrary.data.models.Chapter
import com.freddieptf.mangalibrary.data.models.LibraryItem
import com.freddieptf.mangalibrary.utils.AlphanumComparator
import com.freddieptf.mangalibrary.utils.ChapterTitleComparator
import com.freddieptf.mangalibrary.utils.ChapterUtils
import com.freddieptf.reader.data.ReaderDataManager
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by freddieptf on 9/16/18.
 */
class LibraryViewModel : ViewModel() {

    private var libDirsData: LiveData<List<LibraryItem>>? = null

    fun getLibraryDirs(ctx: Context, libTreeUri: Uri): LiveData<List<LibraryItem>> {
        if (libDirsData == null) {
            libDirsData = LibraryDataManager.getLibraryItems()
            launch {
                genLibDirs(ctx, libTreeUri)
            }
        }
        return libDirsData!!
    }

    private suspend fun genLibDirs(ctx: Context, treeUri: Uri) {
        val libraryDocFile = DocumentFile.fromTreeUri(ctx, treeUri)
        libraryDocFile!!.listFiles().asList().forEach {
            LibraryDataManager.saveLibraryItem(LibraryItem(it.uri, it.name!!, it.listFiles()!!.size, null))
        }
    }

    private lateinit var chapterList: LiveData<List<Chapter>>

    fun getChapters(ctx: Context, dirUri: Uri): LiveData<List<Chapter>> {
        chapterList = LibraryDataManager.getChapters(dirUri)
        launch {
            val result = openMangaDir(ctx, dirUri)
            if(result != null) LibraryDataManager.saveChapters(result)
        }
        return chapterList
    }

    private fun openMangaDir(ctx: Context, dirUri:Uri): List<Chapter>? {

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
            chapter.read = ReaderDataManager.isChapterRead(chapter.parentName, chapter.name)
            chapters.add(chapter)
        } while (cursor.moveToNext())

        Collections.sort(chapters, ChapterTitleComparator())
        return chapters
    }

    companion object {
        fun openChapter(ctx: Context, chapter: Chapter): List<String> {
            val chPath = ChapterUtils.getChapterUrlFromDocID(chapter.docID)
            var file = File(chPath)
            val paths = ArrayList<String>()
            if (!file.isDirectory) {
                file = ChapterUtils.getChapter(ctx.externalCacheDir.absolutePath, chapter)
            }
            file.listFiles().mapTo(paths) { it.absolutePath }
            Collections.sort(paths, AlphanumComparator())
            return paths
        }
    }

}
package com.freddieptf.mangalibrary

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.freddieptf.mangalibrary.data.LibraryDataManager
import com.freddieptf.mangalibrary.data.models.Chapter
import com.freddieptf.mangalibrary.data.models.LibraryItem
import com.freddieptf.mangalibrary.utils.ChapterTitleComparator
import com.freddieptf.mangalibrary.utils.SingleEvent
import com.freddieptf.reader.data.ReaderDataManager
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
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

    private val mangaDirSelection: MutableLiveData<LibraryItem> = MutableLiveData()
    private val chapterList: LiveData<SingleEvent<List<Chapter>>> = Transformations.switchMap(mangaDirSelection) {
        libItem -> getChapterListR(libItem)
    }

    fun getDbChapterList(dirUri: Uri): LiveData<List<Chapter>> = LibraryDataManager.getChaptersLiveData(dirUri)

    fun getChapterList(): LiveData<SingleEvent<List<Chapter>>> = chapterList

    fun setChSelection(dirUri: LibraryItem) {
        mangaDirSelection.value = dirUri
    }

    private fun getChapterListR(item: LibraryItem): LiveData<SingleEvent<List<Chapter>>> {
        val resumedList = MutableLiveData<SingleEvent<List<Chapter>>>()
        launch {
            val list = LibraryDataManager.getChapters(item.dirUri)
            launch(UI) {
                resumedList.value = SingleEvent(list)
            }
        }
        return resumedList
    }

    fun syncChaptersFromDisk(ctx: Context, dirUri: Uri) {
        launch {
            val result = openMangaDir(ctx, dirUri)
            if (result != null) LibraryDataManager.saveChapters(result)
        }
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

}
package com.freddieptf.mangalibrary.detail

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import com.freddieptf.mangalibrary.ChapterUtils
import com.freddieptf.mangalibrary.data.Chapter
import com.freddieptf.mangalibrary.utils.AlphanumComparator
import com.freddieptf.mangalibrary.utils.ChapterTitleComparator
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.util.*
import kotlin.coroutines.experimental.suspendCoroutine

/**
 * Created by freddieptf on 9/1/18.
 */
class Presenter(private val view: Contract.View,
                private val dirUri: Uri) {

    private fun openMangaDir(ctx: Context): List<Chapter>? {

        val manga = Uri.parse(dirUri.path.replace(":", "/")).lastPathSegment
        view.showTitle(manga)

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
                    manga)
            chapters.add(chapter)
        } while (cursor.moveToNext())

        Collections.sort(chapters, ChapterTitleComparator())
        return chapters
    }

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

    fun startLoad(ctx: Context) {
        launch(UI) {
            val result = getChapterData(ctx)
            view.onChaptersLoad(result!!)
        }
    }

    private suspend fun getChapterData(ctx: Context) = suspendCoroutine<List<Chapter>?> {
        val data = openMangaDir(ctx)
        if (data == null) it.resumeWithException(NullPointerException("no data"))
        else it.resume(data)
    }

}
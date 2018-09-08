package com.freddieptf.mangalibrary.detail

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.support.v4.os.EnvironmentCompat
import com.freddieptf.mangalibrary.data.Chapter
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.nio.file.DirectoryStream
import java.nio.file.Files
import kotlin.coroutines.experimental.suspendCoroutine

/**
 * Created by freddieptf on 9/1/18.
 */
class Presenter(private val view: Contract.View,
                private val dirUri: Uri) {

    private fun openMangaDir(ctx: Context): List<Chapter>? {

        val uri = DocumentsContract.buildChildDocumentsUriUsingTree(
                dirUri,
                DocumentsContract.getDocumentId(dirUri)
        )

        println(uri)

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
            val chapter = Chapter(cursor.getString(0), cursor.getString(1), cursor.getString(2))
            chapters.add(chapter)
        } while (cursor.moveToNext())

        return chapters
    }

    fun openChapterDir(ctx: Context, chapter: Chapter): List<String> {
        val file = File("/storage/" + chapter.docID.replace(":", "/"))
        println(file.name + "::" + file.isDirectory + "::" + file.length())
        val paths = ArrayList<String>()
        file.listFiles().mapTo(paths) {it.absolutePath}
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
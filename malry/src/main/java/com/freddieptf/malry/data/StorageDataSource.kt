package com.freddieptf.malry.data

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import com.freddieptf.malry.data.db.models.Chapter
import com.freddieptf.malry.data.db.models.LibraryItem
import com.freddieptf.malry.data.utils.ChapterTitleComparator
import java.util.*

class StorageDataSource(private val ctx: Context) {

    internal suspend fun getLibraryItems(treeUri: Uri): List<LibraryItem> {
        val libraryDocFile = DocumentFile.fromTreeUri(ctx, treeUri)
        return libraryDocFile!!.listFiles().map { LibraryItem(it.uri, it.name!!, it.listFiles().size, null) }
    }

    internal suspend fun getChapters(dirUri: Uri): List<Chapter> {

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

        if (!cursor.moveToFirst()) return mutableListOf()

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
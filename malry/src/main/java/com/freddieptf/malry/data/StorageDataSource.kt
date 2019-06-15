package com.freddieptf.malry.data

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import com.freddieptf.malry.data.db.models.Chapter
import com.freddieptf.malry.data.db.models.LibraryItem
import com.freddieptf.malry.data.utils.ChapterTitleComparator
import java.util.*

class StorageDataSource(private val ctx: Context) {

    companion object {
        const val SOURCE_PKG: Long = 1
    }

    internal suspend fun getLibraryItems(treeUri: Uri): List<LibraryItem> {
        val uri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, DocumentsContract.getTreeDocumentId(treeUri))
        val PROJECTION = arrayOf<String>(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME
        )
        val cursor = ctx.contentResolver.query(uri,
                PROJECTION,
                null,
                null,
                null)
        if (cursor?.moveToFirst() == false) return emptyList()
        val items = mutableListOf<LibraryItem>()
        do {
            if (!cursor.getString(1).startsWith(".nomedia", ignoreCase = true)) {
                val docURI = DocumentsContract.buildDocumentUriUsingTree(treeUri, cursor.getString(0))
                val item = LibraryItem(docURI.toString(), docURI, StorageDataSource.SOURCE_PKG, cursor.getString(1), getChildDirChildCount(docURI), null)
                items.add(item)
            }
        } while (cursor.moveToNext())

        return items
    }

    private fun getChildDirChildCount(dirUri: Uri): Int {
        val uri = DocumentsContract.buildChildDocumentsUriUsingTree(dirUri, DocumentsContract.getDocumentId(dirUri))
        val PROJECTION = arrayOf(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
        val cursor = ctx.contentResolver.query(uri, PROJECTION, null, null, null)
        return if (cursor?.moveToFirst() == false) 0 else cursor.count
    }

    internal suspend fun getChapters(parentDirURI: Uri): List<Chapter> {
        val uri = DocumentsContract.buildChildDocumentsUriUsingTree(
                parentDirURI,
                DocumentsContract.getDocumentId(parentDirURI)
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
        if (cursor?.moveToFirst() == false) return mutableListOf()
        val chapters = ArrayList<Chapter>()
        do {
            val chapter = Chapter(
                    id = cursor.getString(0),
                    docID = cursor.getString(0),
                    title = cursor.getString(1),
                    mimeType = cursor.getString(2),
                    parentID = parentDirURI.toString()) //
            chapters.add(chapter)
        } while (cursor.moveToNext())
        return chapters.apply { sortWith(ChapterTitleComparator()) }
    }

}
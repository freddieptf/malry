package com.freddieptf.malry.data

import android.net.Uri
import com.freddieptf.malry.api.Chapter
import com.freddieptf.malry.api.ChapterProvider
import com.freddieptf.malry.commons.AlphanumComparator
import com.freddieptf.malry.data.cache.ArchiveCacheManager
import com.freddieptf.malry.data.utils.ChapterUtils
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by freddieptf on 9/22/18.
 */
internal class ChapterProvider(var libraryDataManager: DbDataSource) : ChapterProvider() {

    private var chapterIDs = ArrayList<String>()
    private var parentUri: Uri? = null
    private var pos: Int = 0

    private fun openChapter(chapter: Chapter): List<String> {
        val chPath = ChapterUtils.getChapterPathFromDocID(chapter.id)
        var file = File(chPath)
        val paths = ArrayList<String>()
        if (!file.isDirectory) {
            file = ArchiveCacheManager.getChapterFile(chapter)
        }
        file.listFiles().mapTo(paths) { it.absolutePath }
        Collections.sort(paths, AlphanumComparator())
        return paths
    }

    private fun getChapterAtPos(position: Int): Chapter {
        val chID = chapterIDs.get(position)
        val ch = libraryDataManager.getChapter(chID)
        return ch.apply {
            setPaths(openChapter(ch))
        }
    }

    override fun hasNextRead(): Boolean {
        return pos < chapterIDs.size && !chapterIDs.isEmpty()
    }

    override fun getNextRead(): Chapter? {
        if (pos >= chapterIDs.size || chapterIDs.isEmpty()) return null
        pos++
        return getChapterAtPos(pos)
    }

    override fun getCurrentRead(): Chapter {
        return getChapterAtPos(pos)
    }

    override fun hasPreviousRead(): Boolean {
        return pos >= 0 && !chapterIDs.isEmpty()
    }

    override fun getPreviousRead(): Chapter? {
        if (pos <= 0 || chapterIDs.isEmpty()) return null
        pos--
        return getChapterAtPos(pos)
    }

    override fun setLastReadPage(chapterID: String, page: Int, totalPages: Int) {
        libraryDataManager.setLastReadPage(chapterID, page, totalPages)
    }


    override fun getReadList(): List<Chapter> {
        return libraryDataManager.getChapters(parentUri!!)
    }

    override fun setCurrentRead(chapter: Chapter) {
        parentUri = Uri.parse(chapter.parentID)
        if (chapterIDs.isEmpty()) {
            chapterIDs = libraryDataManager.getChapters(parentUri!!).map { it.id } as ArrayList<String>
        }
        pos = chapterIDs.indexOf(chapter.id)
    }

}
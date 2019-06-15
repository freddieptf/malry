package com.freddieptf.malry.data

import com.freddieptf.malry.api.Chapter
import com.freddieptf.malry.api.ChapterProvider
import com.freddieptf.malry.commons.AlphanumComparator
import com.freddieptf.malry.data.cache.ArchiveCacheManager
import com.freddieptf.malry.data.utils.ChapterUtils
import com.freddieptf.malry.tachiyomicompat.TachiyomiSource
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by freddieptf on 9/22/18.
 */
internal class ChapterProvider(private var pos: Int,
                               private val chapters: List<Chapter>,
                               private val dbDataSource: DbDataSource) : ChapterProvider() {

    private val tachiyomiSource = TachiyomiSource()

    private suspend fun openChapter(chapter: Chapter): List<String> {
        var paths = ArrayList<String>()
        if (chapter.docID == null) {
            paths.addAll(tachiyomiSource.getChapterPageUrls(chapter.id, chapter.sourceID))
        } else {
            val chPath = ChapterUtils.getChapterPathFromDocID(chapter.id)
            var file = File(chPath)
            if (!file.isDirectory) {
                file = ArchiveCacheManager.getChapterFile(chapter)
            }
            file.listFiles().mapTo(paths) { it.absolutePath }
            Collections.sort(paths, AlphanumComparator())
        }
        return paths
    }

    private suspend fun getChapterAtPos(position: Int): Chapter {
        val ch = chapters.get(position)
        return ch.apply {
            setPaths(openChapter(ch))
        }
    }

    override fun hasNextRead(): Boolean {
        return pos < chapters.size && !chapters.isEmpty()
    }

    override suspend fun getNextRead(): Chapter? {
        if (pos >= chapters.size || chapters.isEmpty()) return null
        pos++
        return getChapterAtPos(pos)
    }

    override suspend fun getCurrentRead(): Chapter {
        return getChapterAtPos(pos)
    }

    override fun hasPreviousRead(): Boolean {
        return pos >= 0 && !chapters.isEmpty()
    }

    override suspend fun getPreviousRead(): Chapter? {
        if (pos <= 0 || chapters.isEmpty()) return null
        pos--
        return getChapterAtPos(pos)
    }

    override fun setLastReadPage(chapterID: String, page: Int, totalPages: Int) {
        dbDataSource.setLastReadPage(chapterID, page, totalPages)
    }

    override suspend fun getReadList(): List<Chapter> = chapters

    override fun setCurrentRead(chapter: Chapter) {
        pos = chapters.indexOf(chapter)
    }

}
package com.freddieptf.mangalibrary

import com.freddieptf.mangalibrary.data.ArchiveCacheManager
import com.freddieptf.mangalibrary.utils.AlphanumComparator
import com.freddieptf.mangalibrary.utils.ChapterUtils
import com.freddieptf.reader.api.Chapter
import com.freddieptf.reader.api.Provider
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by freddieptf on 9/22/18.
 */
class LocalLibChProvider : Provider() {

    private lateinit var chapters: ArrayList<com.freddieptf.mangalibrary.data.models.Chapter>
    private var pos: Int = 0

    private fun openChapter(chapter: com.freddieptf.mangalibrary.data.models.Chapter): List<String> {
        val chPath = ChapterUtils.getChapterPathFromDocID(chapter.docID)
        var file = File(chPath)
        val paths = ArrayList<String>()
        if (!file.isDirectory) {
            file = ArchiveCacheManager.getChapterFile(chapter)
        }
        file.listFiles().mapTo(paths) { it.absolutePath }
        Collections.sort(paths, AlphanumComparator())
        return paths
    }

    fun setRead(startPos: Int, chapters: List<com.freddieptf.mangalibrary.data.models.Chapter>): LocalLibChProvider {
        this.pos = startPos
        this.chapters = chapters as ArrayList<com.freddieptf.mangalibrary.data.models.Chapter>;
        return this
    }

    override fun hasNextRead(): Boolean {
        return pos < chapters.size && !chapters.isEmpty()
    }

    override fun getNextRead(): Chapter? {
        if (pos >= chapters.size || chapters.isEmpty()) return null
        pos++
        val ch = chapters.get(pos)
        return Chapter(ch.name, ch.parentName).setPaths(openChapter(ch))
    }

    override fun getCurrentRead(): Chapter {
        val ch = chapters.get(pos)
        return Chapter(ch.name, ch.parentName).setPaths(openChapter(ch))
    }

    override fun hasPreviousRead(): Boolean {
        return pos >= 0 && !chapters.isEmpty()
    }

    override fun getPreviousRead(): Chapter? {
        if (pos <= 0 || chapters.isEmpty()) return null
        pos--
        val ch = chapters.get(pos)
        return Chapter(ch.name, ch.parentName).setPaths(openChapter(ch))
    }

    override fun getReadList(): List<Chapter> {
        return chapters.map { it -> Chapter(it.name, it.parentName) }
    }

    override fun setCurrentRead(chapter: Chapter) {
        pos = getReadList().indexOf(chapter)
    }

}
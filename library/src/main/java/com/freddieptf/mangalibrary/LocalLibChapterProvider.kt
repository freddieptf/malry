package com.freddieptf.mangalibrary

import android.content.Context
import com.freddieptf.reader.api.Chapter
import com.freddieptf.reader.api.Provider

/**
 * Created by freddieptf on 9/22/18.
 */
class LocalLibChapterProvider : Provider() {

    private lateinit var chapters: List<com.freddieptf.mangalibrary.data.models.Chapter>
    private var pos: Int = 0
    private var ctx: Context? = null

    fun useCtx(ctx: Context): LocalLibChapterProvider {
        this.ctx = ctx;
        return this
    }

    fun setRead(startPos: Int, chapters: List<com.freddieptf.mangalibrary.data.models.Chapter>): LocalLibChapterProvider {
        this.pos = startPos
        this.chapters = chapters;
        return this
    }

    override fun hasNextRead(): Boolean {
        return pos < chapters.size && !chapters.isEmpty()
    }

    override fun getNextRead(): Chapter? {
        if (pos >= chapters.size || chapters.isEmpty()) return null
        pos++
        val ch = chapters.get(pos)
        return Chapter(ch.name, ch.parentName, LibraryViewModel.openChapter(ctx!!, ch))
    }

    override fun getCurrentRead(): Chapter {
        val ch = chapters.get(pos)
        return Chapter(ch.name, ch.parentName, LibraryViewModel.openChapter(ctx!!, ch))
    }

    override fun hasPreviousRead(): Boolean {
        return pos >= 0 && !chapters.isEmpty()
    }

    override fun getPreviousRead(): Chapter? {
        if (pos <= 0 || chapters.isEmpty()) return null
        pos--
        val ch = chapters.get(pos)
        return Chapter(ch.name, ch.parentName, LibraryViewModel.openChapter(ctx!!, ch))
    }


}
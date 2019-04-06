package com.freddieptf.malry.api

/**
 * Created by freddieptf on 9/22/18.
 */
abstract class ChapterProvider {

    var initialized = false

    open fun initialize() {
        initialized = true
    }

    abstract fun hasNextRead(): Boolean

    abstract fun getNextRead(): Chapter?

    abstract fun getCurrentRead(): Chapter

    abstract fun setCurrentRead(chapter: Chapter)

    abstract fun hasPreviousRead(): Boolean

    abstract fun getPreviousRead(): Chapter?

    abstract fun setLastReadPage(chapterID: String, page: Int, totalPages: Int)

    abstract fun getReadList(): List<Chapter>

}
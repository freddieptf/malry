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

    abstract suspend fun getNextRead(): Chapter?

    abstract suspend fun getCurrentRead(): Chapter

    abstract fun setCurrentRead(chapter: Chapter)

    abstract fun hasPreviousRead(): Boolean

    abstract suspend fun getPreviousRead(): Chapter?

    abstract fun setLastReadPage(chapterID: String, page: Int, totalPages: Int)

    abstract suspend fun getReadList(): List<Chapter>

}
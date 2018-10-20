package com.freddieptf.reader

import androidx.lifecycle.ViewModel
import com.freddieptf.reader.data.ReaderDataManager
import com.freddieptf.reader.data.models.ChReadCache

/**
 * Created by freddieptf on 9/16/18.
 */
internal class ReaderFragViewModel() : ViewModel() {

    fun getLastViewedChPage(parent: String, chapter: String): Int {
        return ReaderDataManager.getCache(parent, chapter)?.page ?: 0
    }

    fun saveLastViewedPage(parent: String, chapterTitle: String, page: Int, totalPages: Int) {
        ReaderDataManager.save(ChReadCache(parent, chapterTitle, page, totalPages))
    }

}
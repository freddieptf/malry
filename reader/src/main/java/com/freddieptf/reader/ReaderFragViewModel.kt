package com.freddieptf.reader

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.freddieptf.reader.data.ReaderDataManager
import com.freddieptf.reader.data.models.ChapterCache

/**
 * Created by freddieptf on 9/16/18.
 */
internal class ReaderFragViewModel(): ViewModel() {

    fun getChCache(parent: String, chapter: String): LiveData<ChapterCache> {
        return ReaderDataManager.getLCache(parent, chapter)
    }

}
package com.freddieptf.reader

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freddieptf.reader.api.Chapter
import com.freddieptf.reader.api.ChapterProvider
import com.freddieptf.reader.data.ReaderDataManager
import com.freddieptf.reader.data.models.ChReadCache
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * Created by freddieptf on 9/16/18.
 */
internal class ReaderFragViewModel() : ViewModel() {

    private var openChapterChan = MutableLiveData<Chapter>()
    private var currentReadChan = MutableLiveData<Chapter>()

    fun getLastViewedChPage(parent: String, chapter: String): Int {
        return ReaderDataManager.getCache(parent, chapter)?.page ?: 0
    }

    fun saveLastViewedPage(parent: String, chapterTitle: String, page: Int, totalPages: Int) {
        ReaderDataManager.save(ChReadCache(parent, chapterTitle, page, totalPages))
    }

    // wat
    fun openChapterChannel(): MutableLiveData<Chapter> {
        return openChapterChan
    }

    fun currentReadChannel(): MutableLiveData<Chapter> = currentReadChan

    fun notifyOpenChapter(chapter: Chapter) {
        openChapterChan.value = chapter
    }

    fun setCurrentRead(chapter: Chapter) {
        currentReadChan.value = chapter
    }

    fun getReadList(): LiveData<List<Chapter>> {
        val data = MutableLiveData<List<Chapter>>()
        launch {
            val l = ChapterProvider.getProvider().getReadList()
            launch(UI) {
                data.value = l
            }
        }
        return data
    }

}
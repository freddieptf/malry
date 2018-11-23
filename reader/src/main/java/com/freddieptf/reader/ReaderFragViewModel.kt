package com.freddieptf.reader

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freddieptf.malry.api.Chapter
import com.freddieptf.malry.commons.SingleEvent

/**
 * Created by freddieptf on 9/16/18.
 */
internal class ReaderFragViewModel() : ViewModel() {

    private var openChapterChan = MutableLiveData<SingleEvent<Chapter>>()
    private var currentReadChan = MutableLiveData<Chapter>()

    fun saveLastViewedPage(chapterID: String, page: Int, totalPages: Int) {
        ChapterProvider.getProvider().setLastReadPage(chapterID, page, totalPages)
    }
    // wat
    fun openChapterChannel(): MutableLiveData<SingleEvent<Chapter>> {
        return openChapterChan
    }

    fun currentReadChannel(): MutableLiveData<Chapter> = currentReadChan

    fun notifyOpenChapter(chapter: Chapter) {
        openChapterChan.value = SingleEvent(chapter)
    }

    fun setCurrentRead(chapter: Chapter) {
        currentReadChan.value = chapter
    }

    fun getReadList(): LiveData<List<Chapter>> = ChapterProvider.getProvider().getReadList()

}
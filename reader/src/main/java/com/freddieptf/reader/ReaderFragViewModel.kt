package com.freddieptf.reader

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freddieptf.malry.api.Chapter
import com.freddieptf.malry.commons.SingleEvent
import com.freddieptf.reader.utils.ReadMode
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Created by freddieptf on 9/16/18.
 */
internal class ReaderFragViewModel() : ViewModel(), CoroutineScope {
    private val job: Job

    init {
        job = Job()
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

    private var openChapterChan = MutableLiveData<SingleEvent<Chapter>>()
    private var currentReadChan = MutableLiveData<Chapter>()
    private var readDirectionChan = MutableLiveData<SingleEvent<ReadMode>>()
    private var chList = MutableLiveData<List<Chapter>>()

    fun saveLastViewedPage(chapterID: String, page: Int, totalPages: Int) {
        // hopefully this runs even when this context is destroyed
        GlobalScope.launch {
            ChapterProvider.getProvider().setLastReadPage(chapterID, page, totalPages)
        }
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

    fun setReadDirection(direction: ReadMode) {
        readDirectionChan.value = SingleEvent(direction)
    }

    fun getReadDirection(): MutableLiveData<SingleEvent<ReadMode>> {
        return readDirectionChan
    }

    fun getReadList(): LiveData<List<Chapter>> {
        launch(Dispatchers.Default) {
            val chs = ChapterProvider.getProvider().getReadList()
            withContext(Dispatchers.Main) {
                chList.value = chs
            }
        }
        return chList
    }

}
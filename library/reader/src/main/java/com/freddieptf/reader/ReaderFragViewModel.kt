package com.freddieptf.reader

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freddieptf.malry.api.Chapter
import com.freddieptf.malry.commons.SingleEvent
import com.freddieptf.reader.utils.ReadMode
import com.freddieptf.reader.utils.ReaderPrefUtils
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Created by freddieptf on 9/16/18.
 */
internal class ReaderFragViewModel : ViewModel(), CoroutineScope {
    private val job: Job

    internal enum class ChapterSwitch { NEXT, PREVIOUS }

    private val chapterSwitchSignal: MutableLiveData<ChapterSwitch>
    private var currentChapterLiveData = MutableLiveData<Chapter>()
    private var readDirectionChan = MutableLiveData<SingleEvent<ReadMode>>()
    private var chList = MutableLiveData<List<Chapter>>()

    init {
        job = Job()
        // lol
        chapterSwitchSignal = MutableLiveData()
        observeChapterSwitch()
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

    fun switchChapter(switch: ChapterSwitch) {
        chapterSwitchSignal.value = switch
    }

    private fun observeChapterSwitch() {
        chapterSwitchSignal.observeForever {
            launch {
                var ch: Chapter? = null
                when (it) {
                    ChapterSwitch.NEXT -> {
                        ch = if (ChapterLoader.getProvider().hasNextRead()) ChapterLoader.getProvider().getNextRead() else null
                    }
                    ChapterSwitch.PREVIOUS -> {
                        ch = if (ChapterLoader.getProvider().hasPreviousRead()) ChapterLoader.getProvider().getPreviousRead() else null
                    }
                }

                ch?.let {
                    notifyCurrentChapterChange(ch)

                }
            }
        }
    }

    fun saveLastViewedPage(chapterID: String, page: Int, totalPages: Int) {
        // hopefully this runs even when this context is destroyed
        GlobalScope.launch(Dispatchers.Default) {
            ChapterLoader.getProvider().setLastReadPage(chapterID, page, totalPages)
        }
    }

    fun initializeChapterProvider() {
        launch {
            if (ChapterLoader.getProvider().initialized) return@launch
            ChapterLoader.getProvider().initialize()
            val ch = ChapterLoader.getProvider().getCurrentRead()
            withContext(Dispatchers.Main) {
                notifyCurrentChapterChange(ch)
            }
        }
    }

    fun observeCurrentRead(): MutableLiveData<Chapter> {
        return currentChapterLiveData
    }

    fun notifyCurrentChapterChange(chapter: Chapter) {
        launch(Dispatchers.Main) {
            ChapterLoader.getProvider().setCurrentRead(chapter)
            var ch: Chapter? = null
            if (chapter.paths?.isEmpty() == true) {
                withContext(Dispatchers.Default) {
                    ch = ChapterLoader.getProvider().getCurrentRead()
                }
            } else ch = chapter
            currentChapterLiveData.value = ch!!
        }
    }

    fun setReadDirection(ctx: Context, direction: ReadMode) {
        launch {
            ReaderPrefUtils.setReadDirection(ctx, direction)
            withContext(Dispatchers.Main) {
                readDirectionChan.value = SingleEvent(direction)
            }
        }
    }

    fun observeReadDirection(): MutableLiveData<SingleEvent<ReadMode>> {
        return readDirectionChan
    }

    fun getReadList(): LiveData<List<Chapter>> {
        launch {
            val chs = ChapterLoader.getProvider().getReadList()
            withContext(Dispatchers.Main) {
                chList.value = chs
            }
        }
        return chList
    }

}
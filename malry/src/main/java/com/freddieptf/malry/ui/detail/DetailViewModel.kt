package com.freddieptf.malry.ui.detail

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.freddieptf.malry.api.Chapter
import com.freddieptf.malry.api.ChapterProvider
import com.freddieptf.malry.data.DataProvider
import com.freddieptf.malry.data.StorageDataSource
import com.freddieptf.malry.tachiyomicompat.TachiyomiSource
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Created by freddieptf on 9/16/18.
 */
class DetailViewModel constructor(var dataProvider: DataProvider) : ViewModel(), CoroutineScope {

    private var job: Job
    private val tachiyomiSource: TachiyomiSource = TachiyomiSource()
    private val updateExternalChapter = MutableLiveData<List<Chapter>>()
    private val externalChapterObserver = Observer<List<Chapter>> {
        launch(Dispatchers.Default) { dataProvider.saveChapters(it) }
    }

    init {
        job = Job()
        updateExternalChapter.observeForever(externalChapterObserver)
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    override fun onCleared() {
        job.cancel()
        updateExternalChapter.removeObserver(externalChapterObserver)
        super.onCleared()
    }


    private val chapterLiveData = MutableLiveData<List<Chapter>>()

    fun getChapterList(libraryItemID: String, itemDirURI: Uri?, sourceID: Long): LiveData<List<Chapter>> {
        if (itemDirURI == null) {
            if (chapterLiveData.value == null) {
                launch(Dispatchers.Default) {
                    tachiyomiSource.getChapterList(libraryItemID, sourceID, chapterLiveData)
                }
            }
            return chapterLiveData
        } else {
            launch(Dispatchers.Default) {
                if (sourceID != StorageDataSource.SOURCE_PKG) {
                    tachiyomiSource.getChapterList(libraryItemID, sourceID, updateExternalChapter)
                } else {
                    dataProvider.saveChapters(itemDirURI)
                }
            }
            return dataProvider.getChapters(libraryItemID)
        }
    }

    fun getChapterProvider(chapter: Chapter): LiveData<ChapterProvider> {
        val rl = MutableLiveData<ChapterProvider>()
        launch(Dispatchers.Default) {
            val provider = dataProvider.getChapterProvider(chapter)
            withContext(Dispatchers.Main) {
                rl.value = provider
            }
        }
        return rl
    }

}
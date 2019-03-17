package com.freddieptf.malry.ui.detail

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freddieptf.malry.api.Chapter
import com.freddieptf.malry.api.ChapterProvider
import com.freddieptf.malry.data.DataProvider
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Created by freddieptf on 9/16/18.
 */
class DetailViewModel constructor(var dataProvider: DataProvider) : ViewModel(), CoroutineScope {

    private var job: Job

    init {
        job = Job()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }


    fun getDbChapterList(libraryItemUri: Uri): LiveData<List<Chapter>> {
        launch(Dispatchers.Default) {
            dataProvider.saveLibraryItemChildren(libraryItemUri)
        }
        return dataProvider.getLibraryItemChildren(libraryItemUri)
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
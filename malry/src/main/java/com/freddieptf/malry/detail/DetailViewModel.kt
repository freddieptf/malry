package com.freddieptf.malry.detail

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freddieptf.malry.api.Chapter
import com.freddieptf.malry.api.ChapterProvider
import com.freddieptf.malry.api.DataProvider
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

    private var libraryItemChildrenLD = MutableLiveData<List<com.freddieptf.malry.api.Chapter>>()

    fun getDbChapterList(libraryItemUri: Uri): LiveData<List<Chapter>> {

        val db = async(start = CoroutineStart.LAZY) {
            val items = mutableListOf<Chapter>()
            withContext(Dispatchers.Default) {
                items.addAll(dataProvider.getLibraryItemChildren(libraryItemUri))
            }
            items
        }

        val refreshedDB = async(start = CoroutineStart.LAZY) {
            val items = mutableListOf<Chapter>()
            withContext(Dispatchers.Default) {
                dataProvider.importLibraryItemChildren(libraryItemUri)
                items.addAll(dataProvider.getLibraryItemChildren(libraryItemUri))
            }
            items
        }

        launch {
            db.start()
            refreshedDB.start()
            libraryItemChildrenLD.value = db.await()
            libraryItemChildrenLD.value = refreshedDB.await()
        }


        return libraryItemChildrenLD
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
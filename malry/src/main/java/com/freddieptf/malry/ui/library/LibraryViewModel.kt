package com.freddieptf.malry.ui.library

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freddieptf.malry.api.Chapter
import com.freddieptf.malry.api.ChapterProvider
import com.freddieptf.malry.api.LibraryItem
import com.freddieptf.malry.data.DataProvider
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Created by freddieptf on 9/16/18.
 */
class LibraryViewModel constructor(var dataProvider: DataProvider) : ViewModel(), CoroutineScope {

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


    fun populateLibrary(libLocationUri: Uri) {
        launch(Dispatchers.Default) {
            dataProvider.saveToLibrary(libLocationUri)
        }
    }

    fun getLibraryItems(): LiveData<List<com.freddieptf.malry.api.LibraryItem>> {
        return dataProvider.getLibraryItems()
    }

    data class LastReadData(val chapter: Chapter?, val provider: ChapterProvider?)

    fun getLastRead(libraryItem: LibraryItem): LiveData<LastReadData> {
        val lr = MutableLiveData<LastReadData>()
        launch(Dispatchers.Default) {
            val chapter = dataProvider.getLastRead(libraryItem)
            if (chapter == null) {
                withContext(Dispatchers.Main) {
                    lr.value = LastReadData(null, null)
                }
            } else {
                val provider = dataProvider.getChapterProvider(chapter)
                withContext(Dispatchers.Main) {
                    lr.value = LastReadData(chapter, provider)
                }
            }
        }
        return lr
    }

}
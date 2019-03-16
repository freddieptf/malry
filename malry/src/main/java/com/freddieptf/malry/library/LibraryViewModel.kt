package com.freddieptf.malry.library

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freddieptf.malry.api.Chapter
import com.freddieptf.malry.api.ChapterProvider
import com.freddieptf.malry.api.LibraryItem
import com.freddieptf.malry.data.LocalStorageProvider
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Created by freddieptf on 9/16/18.
 */
class LibraryViewModel constructor(var dataProvider: LocalStorageProvider) : ViewModel(), CoroutineScope {

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

    private var libraryItemsLD = MutableLiveData<List<com.freddieptf.malry.api.LibraryItem>>()
    private var libraryItemChildrenLD = MutableLiveData<List<com.freddieptf.malry.api.Chapter>>()

    fun populateLibrary(libLocationUri: Uri) {
        launch {
            withContext(Dispatchers.Default) {
                dataProvider.importLibrary(libLocationUri)
            }
            val items: MutableList<LibraryItem> = ArrayList()
            withContext(Dispatchers.Default) {
                items.addAll(dataProvider.getLibraryItems())
            }
            libraryItemsLD.value = items
        }
    }

    fun getLibraryItems(): LiveData<List<com.freddieptf.malry.api.LibraryItem>> {
        val db = async(start = CoroutineStart.LAZY) {
            val items: MutableList<LibraryItem> = ArrayList()
            withContext(Dispatchers.Default) {
                items.addAll(dataProvider.getLibraryItems())
            }
            items
        }

        launch {
            db.start()
            libraryItemsLD.value = db.await()
        }

        return libraryItemsLD
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
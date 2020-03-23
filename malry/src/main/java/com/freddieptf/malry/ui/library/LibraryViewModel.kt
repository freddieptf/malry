package com.freddieptf.malry.ui.library

import android.net.Uri
import androidx.lifecycle.*
import com.freddieptf.malry.api.Chapter
import com.freddieptf.malry.api.ChapterProvider
import com.freddieptf.malry.api.LibraryItem
import com.freddieptf.malry.data.DataProvider
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Created by freddieptf on 9/16/18.
 */
internal class LibraryViewModel constructor(private val dataProvider: DataProvider) : ViewModel(), CoroutineScope {

    private val job: Job
    private val searchInput = MutableLiveData<String>()

    internal data class ViewState(var progress: Boolean = false, var data: List<LibraryItem>, var error: String? = null)

    private val viewData = MediatorLiveData<ViewState>()
    private val dbItemsLiveData = dataProvider.getLibraryItems()
    private val dbItemsObserver = Observer<Any> {}

    init {
        job = Job()
        viewData.value = ViewState(progress = true, data = mutableListOf(), error = null)
        viewData.addSource(searchInput) { term ->
            viewData.value = viewData.value!!.copy(data = combineLatest(term, dbItemsLiveData.value), error = null)
        }
        // @TODO should't depend on this directly like this..first time it will always return an empty list and set progress to false..
        viewData.addSource(dbItemsLiveData) { data ->
            viewData.value = viewData.value!!.copy(progress = false, data = combineLatest(searchInput.value, data), error = null)
        }
        dbItemsLiveData.observeForever(dbItemsObserver)
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        dbItemsLiveData.removeObserver(dbItemsObserver)
        job.cancel()
        super.onCleared()
    }

    private fun combineLatest(searchTerm: String?,
                              localItems: List<LibraryItem>?): List<LibraryItem> {
        return (if (!searchTerm.isNullOrEmpty())
            localItems?.filter { it.title.contains(searchTerm, true) }
        else localItems) ?: mutableListOf()
    }

    fun populateLibrary(libLocationUri: Uri) {
        viewData.value = viewData.value!!.copy(progress = true, error = null)
        launch(Dispatchers.Default) {
            dataProvider.saveToLibrary(libLocationUri)
        }
    }

    fun search(mangaTitle: String) {
        searchInput.value = mangaTitle
    }

    fun getData(): LiveData<ViewState> {
        return viewData
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
                val chs = dataProvider.getChapterList(libraryItem.ID)
                val provider = dataProvider.getChapterProvider(chs.indexOf(chapter), chs)
                withContext(Dispatchers.Main) {
                    lr.value = LastReadData(chapter, provider)
                }
            }
        }
        return lr
    }

}
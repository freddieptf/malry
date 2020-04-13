package com.freddieptf.malry.ui.library

import android.content.Context
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
    private val dbItemsLiveData = dataProvider.getCachedLibraryItems()
    private val dbItemsObserver = Observer<Any> {}
    private var updating = false

    init {
        job = Job()
        viewData.value = ViewState(progress = true, data = mutableListOf(), error = null)
        viewData.addSource(searchInput) { term ->
            viewData.value = viewData.value!!.copy(data = combineLatest(term, dbItemsLiveData.value), error = null)
        }
        viewData.addSource(dbItemsLiveData) { data ->
            viewData.value = viewData.value!!.copy(progress = updating, data = combineLatest(searchInput.value, data), error = null)
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

    fun search(mangaTitle: String) {
        searchInput.value = mangaTitle
    }

    fun triggerUpdate(ctx: Context) {
        launch(Dispatchers.Default) {
            updating = true
            dataProvider.updateLibrary(ctx)
            updating = false
            withContext(Dispatchers.Main) {
                viewData.value = viewData.value!!.copy(progress = updating)
            }
        }
    }

    fun getData(ctx: Context): LiveData<ViewState> {
        triggerUpdate(ctx)
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
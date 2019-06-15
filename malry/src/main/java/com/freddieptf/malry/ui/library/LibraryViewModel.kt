package com.freddieptf.malry.ui.library

import android.net.Uri
import androidx.lifecycle.*
import com.freddieptf.malry.api.Chapter
import com.freddieptf.malry.api.ChapterProvider
import com.freddieptf.malry.api.LibraryItem
import com.freddieptf.malry.data.DataProvider
import com.freddieptf.malry.tachiyomicompat.TachiyomiSource
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Created by freddieptf on 9/16/18.
 */
class LibraryViewModel constructor(private val dataProvider: DataProvider) : ViewModel(), CoroutineScope {

    private val job: Job
    private val sourceSearch: TachiyomiSource = TachiyomiSource()

    private val searchInput = MutableLiveData<String>()
    private val searchResultsLiveData = MutableLiveData<MutableList<LibraryItem>>()
    private val combinedSearchResults = MediatorLiveData<MutableList<LibraryItem>>().apply {
        addSource(searchResultsLiveData) { data ->
            value = (value ?: mutableListOf())
                    .apply { addAll(data); }.distinct().toMutableList()
        }
    }
    private val combinedResults = MediatorLiveData<List<LibraryItem>>()
    private val dbItemsLiveData = dataProvider.getLibraryItems()

    private val dbItemsObserver = Observer<Any> { /**do nothing**/ }

    init {
        job = Job()

        combinedResults.addSource(searchInput) { title ->
            launch(Dispatchers.Default) { sourceSearch.search(title, searchResultsLiveData); };
            combinedResults.value = combineLatest(searchInput, combinedSearchResults, dbItemsLiveData)
        }
        combinedResults.addSource(dbItemsLiveData) { data ->
            combinedResults.value = combineLatest(searchInput, combinedSearchResults, dbItemsLiveData)
        }
        combinedResults.addSource(combinedSearchResults) { data ->
            combinedResults.value = combineLatest(searchInput, combinedSearchResults, dbItemsLiveData)
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

    private fun combineLatest(search: MutableLiveData<String>,
                              externalItems: MutableLiveData<MutableList<LibraryItem>>,
                              localItems: LiveData<List<LibraryItem>>): MutableList<LibraryItem> {

        if (search.value.isNullOrEmpty()) return localItems.value as MutableList<LibraryItem>?
                ?: mutableListOf()

        val searchTerm = search.value!!

        val results: MutableList<LibraryItem> = localItems.value?.filter {
            it.title.contains(searchTerm, true)
        } as MutableList<LibraryItem>? ?: mutableListOf()

        results.addAll(externalItems.value?.filter { it.title.startsWith(searchTerm, true) }
                ?: mutableListOf())

        return results
    }

    fun populateLibrary(libLocationUri: Uri) {
        launch(Dispatchers.Default) {
            dataProvider.saveToLibrary(libLocationUri)
        }
    }

    fun search(mangaTitle: String) {
        searchInput.value = mangaTitle
    }

    fun getLibraryItems(): LiveData<List<com.freddieptf.malry.api.LibraryItem>> {
        return combinedResults
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
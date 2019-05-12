package com.freddieptf.malry.tachiyomicompat

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.freddieptf.malry.api.LibraryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class TachiyomiSourceSearch {

    suspend fun search(mangaTitle: String, resultChan: MutableLiveData<MutableList<LibraryItem>>) = coroutineScope {
        launch {
            val d = TachiyomiCompat.sources.map { source ->
                async {
                    var results: MutableList<LibraryItem>
                    try {
                        results = source.fetchSearchManga(1, mangaTitle, source.getFilterList()).toBlocking().single().mangas.map {
                            LibraryItem(Uri.parse(it.url), it.title, source.name)
                        }.toMutableList()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        results = mutableListOf()
                    }
                    results
                }
            }
            d.forEach {
                launch(Dispatchers.Main) {
                    resultChan.value = it.await()
                }
            }
        }
    }

}
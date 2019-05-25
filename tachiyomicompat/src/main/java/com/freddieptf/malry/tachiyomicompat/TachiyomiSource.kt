package com.freddieptf.malry.tachiyomicompat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.freddieptf.malry.api.Chapter
import com.freddieptf.malry.api.LibraryItem
import eu.kanade.tachiyomi.source.model.SManga
import kotlinx.coroutines.*

class TachiyomiSource {

    private val TAG = TachiyomiSource::class.java.simpleName

    suspend fun search(mangaTitle: String, resultChan: MutableLiveData<MutableList<LibraryItem>>) = coroutineScope {
        launch {
            val d = TachiyomiCompat.sources.map { source ->
                async {
                    var results: MutableList<LibraryItem>
                    try {
                        results = source.fetchSearchManga(1, mangaTitle, source.getFilterList()).toBlocking().single().mangas.map { sManga ->
                            LibraryItem(sManga.url, null, sManga.title, source.id, source.name, sManga.thumbnail_url, "")
                        }.toMutableList()
                    } catch (e: Exception) {
                        Log.e(TAG, "${source.name} // ${e.message}")
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


    suspend fun getChapterList(mangaID: String, sourceID: Long, resultChan: MutableLiveData<List<Chapter>>) = coroutineScope {
        launch {
            val source = TachiyomiCompat.sources.singleOrNull { it.id == sourceID }
            val chapters = mutableListOf<Chapter>()
            source?.let {
                source.fetchChapterList(SManga.create().apply { url = mangaID })
                        .toBlocking()
                        .subscribe(
                                { it.mapTo(chapters) { Chapter(it.url, null, it.name, mangaID, null) } },
                                { it.printStackTrace() })
            }
            withContext(Dispatchers.Main) {
                resultChan.value = chapters
            }
        }
    }

}
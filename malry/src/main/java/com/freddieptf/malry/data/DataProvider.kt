package com.freddieptf.malry.data

import android.content.Context
import android.net.Uri
import android.preference.PreferenceManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.freddieptf.malry.api.Chapter
import com.freddieptf.malry.api.ChapterProvider
import com.freddieptf.malry.api.LibraryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by freddieptf on 11/14/18.
 */
class DataProvider(private val localDbSource: DbDataSource,
                   private val storageDataSource: StorageDataSource) {

    private val LIB_PATHS = "lib_paths"
    private val libURIsLiveData = MutableLiveData<Uri>()

    suspend fun addLibUri(ctx: Context, uri: Uri) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
        val editor = prefs.edit()
        editor.putString(LIB_PATHS, uri.toString())
        editor.apply()
        withContext(Dispatchers.Main) {
            libURIsLiveData.value = uri
        }
    }

    private fun getLibUri(ctx: Context): Uri =
            Uri.parse(PreferenceManager.getDefaultSharedPreferences(ctx).getString(LIB_PATHS, "none"))

    suspend fun getLibUriLiveData(ctx: Context): LiveData<Uri> = coroutineScope {
        launch(Dispatchers.IO) {
            val uri = getLibUri(ctx)
            withContext(Dispatchers.Main) {
                libURIsLiveData.value = uri
            }
        }
        libURIsLiveData
    }

    suspend fun importLibrary(libraryDirURI: Uri) {
        val items = storageDataSource.getLibraryItems(libraryDirURI)
        localDbSource.saveLibraryItems(items)
    }

    fun getCachedLibraryItems(): LiveData<List<LibraryItem>> {
        return localDbSource.getLibraryItemsLiveData()
    }

    suspend fun updateLibrary(ctx: Context) {
        val cachedItems: List<LibraryItem> = localDbSource.getLibraryItems()
        val updated = mutableListOf<LibraryItem>()
        storageDataSource.getLibraryItems(getLibUri(ctx)).forEach { s ->
            val cached = cachedItems.firstOrNull { it.ID == s.ID }
            if (cached == null || cached != s) {
                updated.add(s)
            }
        }
        localDbSource.saveLibraryItems(updated)
    }

    suspend fun saveChapters(dirUri: Uri) {
        localDbSource.saveChapters(storageDataSource.getChapters(dirUri))
    }

    suspend fun saveChapters(chapters: List<Chapter>) {
        localDbSource.saveChapters(chapters.map {
            com.freddieptf.malry.data.db.models.Chapter(it.id, it.docID, it.title, "", it.parentID)
        })
    }

    fun getChapters(libraryItemID: String): LiveData<List<com.freddieptf.malry.api.Chapter>> {
        return localDbSource.getChaptersLive(libraryItemID)
    }

    fun getChapterList(libraryItemID: String): List<com.freddieptf.malry.api.Chapter> {
        return localDbSource.getChapters(libraryItemID)
    }


    fun getLastRead(libraryItem: com.freddieptf.malry.api.LibraryItem): com.freddieptf.malry.api.Chapter? {
        return localDbSource.getLastRead(libraryItem.ID)
    }

    fun getChapterProvider(currentPos: Int, chapters: List<com.freddieptf.malry.api.Chapter>): ChapterProvider {
        return ChapterProvider(currentPos, chapters, localDbSource)
    }

}
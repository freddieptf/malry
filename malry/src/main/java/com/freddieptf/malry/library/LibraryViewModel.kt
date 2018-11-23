package com.freddieptf.malry.library

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.freddieptf.malry.ProviderManager
import com.freddieptf.malry.api.Chapter
import com.freddieptf.malry.api.LibraryItem
import com.freddieptf.malry.commons.SingleEvent

/**
 * Created by freddieptf on 9/16/18.
 */
class LibraryViewModel constructor(val dataProvider: ProviderManager) : ViewModel() {

    fun getLibraryDirs(ctx: Context, libTreeUri: Uri): LiveData<List<LibraryItem>> {
        return dataProvider.getLibraryItems()
    }

    fun getDbChapterList(libraryItemUri: Uri): LiveData<List<Chapter>> {
        return dataProvider.openLibraryItem(libraryItemUri)
    }

}
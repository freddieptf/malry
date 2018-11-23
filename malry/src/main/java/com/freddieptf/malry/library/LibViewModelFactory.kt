package com.freddieptf.malry.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.freddieptf.malry.ProviderManager
import javax.inject.Inject

/**
 * Created by freddieptf on 11/17/18.
 */
class LibViewModelFactory @Inject constructor(val dataProvider: ProviderManager) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
            return LibraryViewModel(dataProvider) as T
        }
        throw IllegalArgumentException("Unknown class ${modelClass.simpleName}")
    }

}
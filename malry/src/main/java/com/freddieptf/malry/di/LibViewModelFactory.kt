package com.freddieptf.malry.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.freddieptf.malry.data.DataProvider
import com.freddieptf.malry.ui.detail.DetailViewModel
import com.freddieptf.malry.ui.intro.IntroViewModel
import com.freddieptf.malry.ui.library.LibraryViewModel
import javax.inject.Inject

/**
 * Created by freddieptf on 11/17/18.
 */
class LibViewModelFactory @Inject constructor(val context: Context, val dataProvider: DataProvider) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
            return LibraryViewModel(dataProvider) as T
        } else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(dataProvider) as T
        } else if (modelClass.isAssignableFrom(IntroViewModel::class.java)) {
            return IntroViewModel(context, dataProvider) as T
        }
        throw IllegalArgumentException("Unknown class ${modelClass.simpleName}")
    }

}
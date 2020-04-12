package com.freddieptf.malry.ui.intro

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freddieptf.malry.data.DataProvider
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class IntroViewModel(private val ctx: Context, private val provider: DataProvider) : ViewModel(), CoroutineScope {

    private val importProgressState = MutableLiveData<Boolean>()
    private val job: Job

    init {
        job = Job()
        launch(Dispatchers.Main) {
            provider.getLibUri(ctx).observeForever {
                if (it.toString() != "none") importLibrary(it)
            }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun addLibraryURI(context: Context, uri: Uri) {
        launch { provider.addLibUri(context, uri) }
    }

    fun getImportState(): LiveData<Boolean> = importProgressState

    private fun importLibrary(uri: Uri) {
        launch {
            withContext(Dispatchers.Main) {
                importProgressState.value = true
            }
            provider.importLibrary(uri)
            withContext(Dispatchers.Main) {
                importProgressState.value = false
            }
        }
    }

}
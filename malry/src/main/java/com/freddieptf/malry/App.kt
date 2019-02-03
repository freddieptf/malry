package com.freddieptf.malry

import android.app.Application
import android.net.Uri
import android.preference.PreferenceManager
import com.facebook.stetho.Stetho
import com.freddieptf.malry.data.ArchiveCacheManager
import com.freddieptf.malry.di.*
import com.freddieptf.malry.library.LibraryPrefs
import com.freddieptf.mangatest.R
import com.squareup.leakcanary.LeakCanary

/**
 * Created by freddieptf on 22/09/16.
 */

class App : Application() {

    lateinit var component: AppComponent
        get

    lateinit var dataProviderComponent: DataProviderComponent
        private set
        get

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)

        component = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        updateDataProvider(LibraryPrefs.getLibUri(this)?: Uri.EMPTY)

        initChCache(null)

    }

    fun updateDataProvider(uri: Uri) {
        println("update the damn uri $uri")
        dataProviderComponent = component.dataProviderComponent(DataProviderModule(uri))
    }

    /**
     * @maxSize size in MBs
     */
    fun initChCache(maxSize: Long?) {
        var maxCacheSize = maxSize?: PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.cbz_cache_size_pref_key), R.integer.cbz_cache_default_size.toString())
                .toLong()
        maxCacheSize *= 1024 * 1024
        ArchiveCacheManager.useCache(externalCacheDir!!.absolutePath + "/archives", maxCacheSize)
    }

}

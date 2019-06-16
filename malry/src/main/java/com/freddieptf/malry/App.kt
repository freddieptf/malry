package com.freddieptf.malry

import android.app.Application
import android.preference.PreferenceManager
import com.facebook.stetho.Stetho
import com.freddieptf.imageloader.ImageLoader
import com.freddieptf.malry.data.cache.ArchiveCacheManager
import com.freddieptf.malry.data.db.LibraryDB
import com.freddieptf.malry.di.AppComponent
import com.freddieptf.malry.di.AppModule
import com.freddieptf.malry.di.DaggerAppComponent
import com.freddieptf.malry.tachiyomicompat.TachiyomiCompat
import com.freddieptf.mangatest.R
import com.squareup.leakcanary.LeakCanary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by freddieptf on 22/09/16.
 */

class App : Application() {

    lateinit var component: AppComponent
        get

    @Inject
    lateinit var db: LibraryDB

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)

        component = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        component.inject(this)

        initImageLoader()
        TachiyomiCompat.init(this)
        initChCache(null)

        GlobalScope.launch(Dispatchers.Default) {
            TachiyomiCompat.createSourceManager(baseContext, db.MangaSourceDao())
        }

    }

    /**
     * @maxSize size in MBs
     */
    fun initChCache(maxSize: Long?) {
        var maxCacheSize = maxSize ?: PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.cbz_cache_size_pref_key), R.integer.cbz_cache_default_size.toString())
                .toLong()
        maxCacheSize *= 1024 * 1024
        ArchiveCacheManager.useCache(externalCacheDir!!.absolutePath + "/archives", maxCacheSize)
    }

    fun initImageLoader() {
        var maxCacheSize = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.image_cache_size_limit_key), R.integer.img_cache_default_size.toString())
                .toLong()
        ImageLoader.init(this, maxCacheSize * 1024 * 1024)
    }

}

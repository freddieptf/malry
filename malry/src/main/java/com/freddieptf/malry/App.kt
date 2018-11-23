package com.freddieptf.malry

import android.app.Application
import android.preference.PreferenceManager
import com.facebook.stetho.Stetho
import com.freddieptf.malry.di.AppComponent
import com.freddieptf.malry.di.AppModule
import com.freddieptf.malry.di.DaggerAppComponent
import com.freddieptf.localstorage.data.ArchiveCacheManager
import com.freddieptf.mangatest.R
import com.squareup.leakcanary.LeakCanary

/**
 * Created by freddieptf on 22/09/16.
 */

class App : Application() {

    lateinit var component: AppComponent
        get

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)

        component = DaggerAppComponent.builder().appModule(AppModule(this)).build()

        initChCache(null)

    }

    /**
     * @maxSize size in MBs
     */
    fun initChCache(maxSize: Long?) {
        var maxCacheSize = maxSize?: PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.chapter_cache_size_pref_key), "100")
                .toLongOrNull()

        if(maxCacheSize != null) maxCacheSize = maxCacheSize * 1024 * 1024

        ArchiveCacheManager.useCache(externalCacheDir!!.absolutePath + "/archives", maxCacheSize)
    }

}

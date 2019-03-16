package com.freddieptf.malry

import android.app.Application
import android.preference.PreferenceManager
import com.facebook.stetho.Stetho
import com.freddieptf.malry.data.ArchiveCacheManager
import com.freddieptf.malry.di.AppComponent
import com.freddieptf.malry.di.AppModule
import com.freddieptf.malry.di.DaggerAppComponent
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
                .getString(getString(R.string.cbz_cache_size_pref_key), R.integer.cbz_cache_default_size.toString())
                .toLong()
        maxCacheSize *= 1024 * 1024
        ArchiveCacheManager.useCache(externalCacheDir!!.absolutePath + "/archives", maxCacheSize)
    }

}

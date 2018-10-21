package com.freddieptf.mangatest

import android.app.Application
import android.preference.PreferenceManager
import androidx.room.Room
import com.freddieptf.mangalibrary.data.ArchiveCacheManager
import com.freddieptf.mangalibrary.data.LibraryDataManager
import com.freddieptf.reader.data.ReaderDataManager
import com.squareup.leakcanary.LeakCanary

/**
 * Created by freddieptf on 22/09/16.
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)

        val appDb = Room.databaseBuilder<AppDb>(this, AppDb::class.java, "app.db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()

        ReaderDataManager.use(appDb)
        LibraryDataManager.use(appDb)
        initChCache(null)

    }

    /**
     * @maxSize size in MBs
     */
    fun initChCache(maxSize: Long?) {
        var maxCacheSize = maxSize?: PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.chapter_cache_size_pref_key), null)
                .toLongOrNull()

        if(maxCacheSize != null) maxCacheSize = maxCacheSize * 1024 * 1024

        ArchiveCacheManager.useCache(externalCacheDir!!.absolutePath + "/archives", maxCacheSize)
    }

}

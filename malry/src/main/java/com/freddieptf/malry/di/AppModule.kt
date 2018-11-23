package com.freddieptf.malry.di

import android.content.Context
import android.net.Uri
import androidx.room.Room
import com.freddieptf.malry.ProviderManager
import com.freddieptf.malry.api.DataProvider
import com.freddieptf.malry.library.LibraryPrefs
import com.freddieptf.localstorage.LocalStorageProvider
import com.freddieptf.localstorage.data.LibraryDB
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by freddieptf on 11/17/18.
 */
@Module
class AppModule(private val context: Context) {

    @Provides
    fun provideContext(): Context = context

    @Provides
    fun provideLibraryDB(context: Context): LibraryDB {
        return Room.databaseBuilder(context, LibraryDB::class.java, "local.db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
    }

    @Provides
    @Singleton
    fun provideDataProvider(context: Context, db: LibraryDB): DataProvider
            = LocalStorageProvider(context, db,LibraryPrefs.getLibUri(context)?: Uri.EMPTY)

    @Provides
    @Singleton
    fun provideDataProviderManager(storageProvider: DataProvider): ProviderManager
            = ProviderManager(storageProvider)

}
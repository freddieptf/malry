package com.freddieptf.malry.di

import android.content.Context
import com.freddieptf.malry.api.DataProvider
import com.freddieptf.malry.data.LibraryDB
import com.freddieptf.malry.data.LocalStorageProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by freddieptf on 11/23/18.
 */
@Module()
class DataProviderModule {

    @Provides
    @Singleton
    fun provideDataProvider(context: Context, db: LibraryDB): DataProvider = LocalStorageProvider(context, db)

}
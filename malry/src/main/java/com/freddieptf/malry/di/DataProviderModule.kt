package com.freddieptf.malry.di

import android.content.Context
import android.net.Uri
import com.freddieptf.localstorage.LocalStorageProvider
import com.freddieptf.localstorage.data.LibraryDB
import com.freddieptf.malry.ProviderManager
import com.freddieptf.malry.api.DataProvider
import dagger.Module
import dagger.Provides

/**
 * Created by freddieptf on 11/23/18.
 */
@Module()
class DataProviderModule(val uri: Uri) {

    @Provides
    @LibLocationScope
    fun provideDataProvider(context: Context, db: LibraryDB): DataProvider
            = LocalStorageProvider(context, db, uri)

    @Provides
    @LibLocationScope
    fun provideDataProviderManager(storageProvider: DataProvider): ProviderManager
            = ProviderManager(storageProvider)

}
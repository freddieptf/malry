package com.freddieptf.malry.di

import android.content.Context
import androidx.room.Room
import com.freddieptf.malry.data.DataProvider
import com.freddieptf.malry.data.DbDataSource
import com.freddieptf.malry.data.db.LibraryDB
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by freddieptf on 11/23/18.
 */
@Module()
class DataProviderModule {

    @Provides
    fun provideDb(context: Context): LibraryDB =
            Room.databaseBuilder(context, LibraryDB::class.java, "local.db")
                    .fallbackToDestructiveMigration()
                    .build()

    @Provides
    @Singleton
    fun provideLibraryDbSource(db: LibraryDB): DbDataSource = DbDataSource(db)

    @Provides
    @Singleton
    fun provideDataProvider(context: Context, dbSource: DbDataSource): DataProvider = DataProvider(context, dbSource)

}
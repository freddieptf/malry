package com.freddieptf.malry.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.freddieptf.malry.data.DataProvider
import com.freddieptf.malry.data.DbDataSource
import com.freddieptf.malry.data.StorageDataSource
import com.freddieptf.malry.data.db.LibraryDB
import com.freddieptf.malry.tachiyomicompat.data.MangaSource
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Singleton

/**
 * Created by freddieptf on 11/23/18.
 */
@Module()
class DataProviderModule {

    @Provides
    fun provideDb(context: Context): LibraryDB {
        var libraryDB: LibraryDB? = null
        libraryDB = Room.databaseBuilder(context, LibraryDB::class.java, "local.db")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        GlobalScope.launch(Dispatchers.Default) {
                            libraryDB!!.MangaSourceDao().insert(
                                    MangaSource(StorageDataSource.SOURCE_PKG, "", "", "Local Storage")
                            )
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
        return libraryDB
    }

    @Provides
    @Singleton
    fun provideLibraryDbSource(db: LibraryDB): DbDataSource = DbDataSource(db)

    @Provides
    @Singleton
    fun provideStorageDataSource(ctx: Context): StorageDataSource = StorageDataSource(ctx)

    @Provides
    @Singleton
    fun provideDataProvider(dbSource: DbDataSource,
                            storageDataSource: StorageDataSource): DataProvider =
            DataProvider(dbSource, storageDataSource)

}
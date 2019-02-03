package com.freddieptf.malry.di

import android.content.Context
import androidx.room.Room
import com.freddieptf.malry.data.LibraryDB
import dagger.Module
import dagger.Provides

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

}
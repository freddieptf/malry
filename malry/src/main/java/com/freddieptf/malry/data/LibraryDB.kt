package com.freddieptf.malry.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.freddieptf.malry.data.dao.ChapterDao
import com.freddieptf.malry.data.dao.LibraryDao
import com.freddieptf.malry.data.models.Chapter
import com.freddieptf.malry.data.models.LibraryItem

/**
 * Created by freddieptf on 9/17/18.
 */
@Database(entities = [LibraryItem::class, Chapter::class], version = 3)
abstract class LibraryDB: RoomDatabase() {

    internal abstract fun LibraryItemDao(): LibraryDao

    internal abstract fun ChapterDao(): ChapterDao

}
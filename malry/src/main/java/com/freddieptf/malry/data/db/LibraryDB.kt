package com.freddieptf.malry.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.freddieptf.malry.data.db.dao.ChapterDao
import com.freddieptf.malry.data.db.dao.LibraryDao
import com.freddieptf.malry.data.db.models.Chapter
import com.freddieptf.malry.data.db.models.LibraryItem
import com.freddieptf.malry.tachiyomicompat.data.MangaSource
import com.freddieptf.malry.tachiyomicompat.data.MangaSourceDao

/**
 * Created by freddieptf on 9/17/18.
 */
@Database(entities = [LibraryItem::class, Chapter::class, MangaSource::class], version = 5)
abstract class LibraryDB: RoomDatabase() {

    internal abstract fun LibraryItemDao(): LibraryDao

    internal abstract fun ChapterDao(): ChapterDao

    internal abstract fun MangaSourceDao(): MangaSourceDao

}
package com.freddieptf.mangatest;

import com.freddieptf.mangalibrary.data.LibraryDB;
import com.freddieptf.mangalibrary.data.models.Chapter;
import com.freddieptf.mangalibrary.data.models.LibraryItem;
import com.freddieptf.reader.data.ReaderDB;
import com.freddieptf.reader.data.models.ChReadCache;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * Created by freddieptf on 9/9/18.
 */
@Database(entities = {ChReadCache.class, LibraryItem.class, Chapter.class}, version = 4)
public abstract class AppDb extends RoomDatabase
        implements ReaderDB, LibraryDB {

}

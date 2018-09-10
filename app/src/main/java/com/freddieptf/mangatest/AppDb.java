package com.freddieptf.mangatest;

import com.freddieptf.reader.data.ReaderDB;
import com.freddieptf.reader.data.models.ChapterCache;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * Created by freddieptf on 9/9/18.
 */
@Database(entities = {ChapterCache.class}, version = 1)
public abstract class AppDb extends RoomDatabase implements ReaderDB {

}

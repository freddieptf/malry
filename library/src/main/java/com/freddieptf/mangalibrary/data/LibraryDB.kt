package com.freddieptf.mangalibrary.data

import com.freddieptf.mangalibrary.data.dao.LibraryDao

/**
 * Created by freddieptf on 9/17/18.
 */
interface LibraryDB {
    fun LibraryItemDao(): LibraryDao
}
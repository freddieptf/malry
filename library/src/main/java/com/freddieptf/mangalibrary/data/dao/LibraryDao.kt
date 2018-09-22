package com.freddieptf.mangalibrary.data.dao

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.room.*
import com.freddieptf.mangalibrary.data.models.Chapter
import com.freddieptf.mangalibrary.data.models.LibraryItem
import com.freddieptf.mangalibrary.utils.DBTypeConverters

/**
 * Created by freddieptf on 9/17/18.
 */
@Dao
@TypeConverters(DBTypeConverters::class)
interface LibraryDao {

    @Query("SELECT * from library")
    fun getLibraryItems(): LiveData<List<LibraryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveLibraryItem(data: LibraryItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveChapters(data: List<Chapter>)

    @Query("SELECT * FROM chapters WHERE parentUri=:uri")
    fun getChapters(uri: Uri): LiveData<List<Chapter>>

}
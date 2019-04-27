package com.freddieptf.malry.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.freddieptf.malry.data.db.models.LibraryItem
import com.freddieptf.malry.data.utils.DBTypeConverters

/**
 * Created by freddieptf on 9/17/18.
 */
@Dao
@TypeConverters(DBTypeConverters::class)
internal interface LibraryDao {

    @Query("SELECT * FROM library")
    fun getLibraryItems(): LiveData<List<LibraryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveLibraryItem(data: LibraryItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveLibraryItems(data: List<LibraryItem>)

}
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

    @Query("SELECT lib.ID, dirUri as dirURI, sourceID, lib.title as title, itemCount, coverImg, manga_source.name as sourceName " +
            "FROM library as lib left join manga_source on lib.sourceID=manga_source.id")
    fun getLibraryItems(): LiveData<List<com.freddieptf.malry.api.LibraryItem>>

    @Query("SELECT lib.ID, dirUri as dirURI, sourceID, lib.title as title, itemCount, coverImg, manga_source.name as sourceName " +
            "FROM library as lib left join manga_source on lib.sourceID=manga_source.id WHERE lib.ID=:ID")
    fun getLibraryItem(ID: String): com.freddieptf.malry.api.LibraryItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveLibraryItem(data: LibraryItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveLibraryItems(data: List<LibraryItem>)

}
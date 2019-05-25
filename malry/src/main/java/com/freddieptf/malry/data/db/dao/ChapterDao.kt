package com.freddieptf.malry.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.freddieptf.malry.data.db.models.Chapter
import com.freddieptf.malry.data.utils.DBTypeConverters

/**
 * Created by freddieptf on 11/13/18.
 */
@Dao
@TypeConverters(DBTypeConverters::class)
internal interface ChapterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveChapters(data: List<Chapter>)

    @Query("SELECT * FROM chapters WHERE parentID=:ID")
    fun getChaptersLiveData(ID: String): LiveData<List<Chapter>>

    @Query("SELECT * FROM chapters WHERE parentID=:ID")
    fun getChapters(ID: String): List<Chapter>

    @Query("SELECT * FROM chapters WHERE ID=:chapterID")
    fun getChapter(chapterID: String): Chapter

    @Query("SELECT * FROM chapters WHERE parentID=:libraryItemID ORDER BY lastRead DESC LIMIT 1")
    fun getLastReadChapter(libraryItemID: String): Chapter?

    @Query("UPDATE chapters SET lastReadPage=:page, " +
            "totalPages=:totalPages, " +
            "lastRead=:lastRead " +
            "WHERE ID=:chapterID")
    fun setLastReadPage(chapterID: String, page: Int, totalPages: Int, lastRead: Long)

}
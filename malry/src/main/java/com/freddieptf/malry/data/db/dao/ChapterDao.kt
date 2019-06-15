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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveChapters(data: List<Chapter>)

    @Query("SELECT chs.*, library.title as parentTitle, manga_source.id as sourceID " +
            "FROM chapters as chs " +
            "JOIN library on chs.parentID=library.ID " +
            "JOIN manga_source on library.sourceID=manga_source.id " +
            "WHERE chs.parentID=:parentID")
    fun getChaptersLiveData(parentID: String): LiveData<List<com.freddieptf.malry.api.Chapter>>

    @Query("SELECT chs.*, library.title as parentTitle, manga_source.id as sourceID " +
            "FROM chapters as chs " +
            "JOIN library on chs.parentID=library.ID " +
            "JOIN manga_source on library.sourceID=manga_source.id " +
            "WHERE chs.parentID=:parentID")
    fun getChapters(parentID: String): List<com.freddieptf.malry.api.Chapter>

    @Query("SELECT chs.*, library.title as parentTitle, manga_source.id as sourceID " +
            "FROM chapters as chs " +
            "JOIN library on chs.parentID=library.ID " +
            "JOIN manga_source on library.sourceID=manga_source.id " +
            "WHERE chs.ID=:chapterID")
    fun getChapter(chapterID: String): com.freddieptf.malry.api.Chapter

    @Query("SELECT chs.*, library.title as parentTitle, manga_source.id as sourceID " +
            "FROM chapters as chs " +
            "JOIN library on chs.parentID=library.ID " +
            "JOIN manga_source on library.sourceID=manga_source.id " +
            "WHERE chs.parentID=:parentID ORDER BY chs.lastRead DESC LIMIT 1")
    fun getLastReadChapter(parentID: String): com.freddieptf.malry.api.Chapter?

    @Query("UPDATE chapters SET lastReadPage=:page, " +
            "totalPages=:totalPages, " +
            "lastRead=:lastRead " +
            "WHERE ID=:chapterID")
    fun setLastReadPage(chapterID: String, page: Int, totalPages: Int, lastRead: Long)

}
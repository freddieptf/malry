package com.freddieptf.malry.tachiyomicompat.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MangaSourceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(source: MangaSource)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sources: List<MangaSource>)

    @Query("SELECT * FROM manga_source")
    fun getSources(): MutableList<MangaSource>

    @Query("SELECT * FROM manga_source WHERE id=:id")
    fun getSource(id: String): MangaSource

}
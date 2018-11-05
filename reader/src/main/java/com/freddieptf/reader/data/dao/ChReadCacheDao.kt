package com.freddieptf.reader.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.freddieptf.reader.data.models.ChReadCache

/**
 * Created by freddieptf on 9/9/18.
 */
@Dao
interface ChReadCacheDao {

    @Query("SELECT * FROM chapter_cache")
    fun get(): LiveData<List<ChReadCache>>

    @Query("SELECT * FROM chapter_cache WHERE manga_name=:manga AND id=:chapterTitle")
    fun get(manga: String, chapterTitle: String): ChReadCache?

    @Query("SELECT * FROM chapter_cache WHERE manga_name=:manga ORDER BY rowid DESC LIMIT 1")
    fun getLastRead(manga: String): ChReadCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(cache: ChReadCache)

}
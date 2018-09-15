package com.freddieptf.reader.data.dao

import androidx.room.*
import com.freddieptf.reader.data.models.ChapterCache

/**
 * Created by freddieptf on 9/9/18.
 */
@Dao
interface ChapterCacheDao {

    @Query("SELECT * FROM chapter_cache WHERE id=:id")
    fun get(id: String): ChapterCache

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(cache: ChapterCache)

}
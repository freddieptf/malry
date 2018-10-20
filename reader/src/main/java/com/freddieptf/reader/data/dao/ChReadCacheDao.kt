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

    @Query("SELECT * FROM chapter_cache WHERE id=:id")
    fun get(id: String): ChReadCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(cache: ChReadCache)

}
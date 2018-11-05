package com.freddieptf.reader.data.models

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by freddieptf on 9/9/18.
 */
@Entity(tableName = "chapter_cache")
class ChReadCache constructor(manga: String, chapterTitle: String, page: Int, totalPages: Int) {

    @ColumnInfo(name = "manga_name")
    var manga: String
        get
        set

    var page: Int
        get
        set

    var totalPages: Int
        get
        set

    var pagePath: String = ""
        get
        set

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    var chapterTitle: String
        get
        set

    init {
        this.page = page
        this.chapterTitle = chapterTitle
        this.totalPages = totalPages
        this.manga = manga
    }

    override fun toString(): String {
        return chapterTitle + "::" + page + "/" + totalPages
    }

}
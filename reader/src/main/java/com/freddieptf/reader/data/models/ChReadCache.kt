package com.freddieptf.reader.data.models

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Created by freddieptf on 9/9/18.
 */
@Entity(tableName = "chapter_cache")
class ChReadCache constructor(chID: String, page: Int, totalPages: Int) {

    constructor(parent: String, chapter: String, page: Int, totalPages: Int) :
            this(parent + "/" + chapter, page, totalPages) {
        this.parent = parent
        this.chapter = chapter
    }

    @Ignore lateinit var parent: String
    @Ignore lateinit var chapter: String

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
    var chID: String
        get
        set

    init {
        this.page = page
        this.chID = chID
        this.totalPages = totalPages
    }

    override fun toString(): String {
        return chID + "::" + page + "/" + totalPages
    }

}
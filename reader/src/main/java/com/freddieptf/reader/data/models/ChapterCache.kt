package com.freddieptf.reader.data.models

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Created by freddieptf on 9/9/18.
 */
@Entity(tableName = "chapter_cache")
class ChapterCache constructor(id: String, page: Int, totalPages: Int) {

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
    var id: String
        get
        set

    init {
        this.page = page
        this.id = id
        this.totalPages = totalPages
    }

    override fun toString(): String {
        return id + "::" + page + "/" + totalPages
    }

}
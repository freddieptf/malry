package com.freddieptf.malry.api

import java.util.*

/**
 * Created by freddieptf on 9/22/18.
 */
data class Chapter(val id: String,
                   val docID: String?, // from the document provider
                   val title: String,
                   val parentID: String,
                   var parentTitle: String?) {

    var lastReadPage: Int = 0
        set
        get

    var totalPages: Int = 0
        set
        get

    var paths: List<String> = ArrayList()
        get

    fun setPaths(paths: List<String>): Chapter {
        this.paths = paths
        return this
    }

    override fun equals(other: Any?): Boolean {
        return other is Chapter && other.id == id && other.parentID == parentID
    }
}
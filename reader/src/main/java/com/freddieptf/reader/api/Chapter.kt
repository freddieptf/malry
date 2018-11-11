package com.freddieptf.reader.api

/**
 * Created by freddieptf on 9/22/18.
 */
data class Chapter(val chapter: String, val parent:String){
    var paths: List<String> = ArrayList()
        get

    fun setPaths(paths: List<String>): Chapter {
        this.paths = paths
        return this
    }

    override fun equals(other: Any?): Boolean {
        return other is Chapter && other.chapter == chapter && other.parent == parent
    }
}
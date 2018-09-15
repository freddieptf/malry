package com.freddieptf.mangalibrary.data

/**
 * Created by freddieptf on 9/1/18.
 */
data class Chapter(val docID: String,
                   val name: String,
                   val mimeType: String,
                   val parent: String) {

    var read: Boolean = false
        set
        get
}
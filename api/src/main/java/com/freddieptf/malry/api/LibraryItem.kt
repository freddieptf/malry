package com.freddieptf.malry.api

import android.net.Uri

/**
 * Created by freddieptf on 11/13/18.
 */
data class LibraryItem(val ID: String,
                       val dirURI: Uri?,
                       val title: String,
                       val sourceID: Long,
                       val sourceName: String?,
                       val coverImg: String?,
                       val type: String?) {

    var itemCount: Int = 0
        get
        set

}
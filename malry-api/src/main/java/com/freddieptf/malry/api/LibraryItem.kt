package com.freddieptf.malry.api

import android.net.Uri

/**
 * Created by freddieptf on 11/13/18.
 */
data class LibraryItem(val uri: Uri, val title: String, val type: String) {
    var itemCount: Int = 0
        get
        set
}
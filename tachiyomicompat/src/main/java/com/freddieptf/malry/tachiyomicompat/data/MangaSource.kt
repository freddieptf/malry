package com.freddieptf.malry.tachiyomicompat.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * model we use to keep track of loaded tachiyomi extensions
 *
 * @param id: the package name of the loaded extension
 * @param name: the name of the extension
 *
 * */
@Entity(tableName = "manga_source")
data class MangaSource(@PrimaryKey val id: Long, val extensionPkg: String, val extensionClass: String, val name: String) {

    var installed: Boolean = false
        get
        set

    var lang: String? = null
        get
        set

}
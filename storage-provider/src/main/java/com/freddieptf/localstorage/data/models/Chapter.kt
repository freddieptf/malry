package com.freddieptf.localstorage.data.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.freddieptf.localstorage.utils.DBTypeConverters

/**
 * Created by freddieptf on 9/1/18.
 */
@Entity(tableName = "chapters")
@TypeConverters(DBTypeConverters::class)
@ForeignKey(entity = LibraryItem::class,
        parentColumns = ["dirUri"],
        childColumns = ["parentUri"],
        onDelete = ForeignKey.CASCADE)
internal data class Chapter(@PrimaryKey val docID: String,
                   val name: String,
                   val mimeType: String,
                   val parentName: String,
                   val parentUri: Uri) {

    var lastReadPage: Int = 0
        set
        get

    var totalPages: Int = 0
        get
        set

    var lastRead: Long = -1

}
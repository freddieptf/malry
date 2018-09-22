package com.freddieptf.mangalibrary.data.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.freddieptf.mangalibrary.utils.DBTypeConverters

/**
 * Created by freddieptf on 9/1/18.
 */
@Entity(tableName = "chapters")
@TypeConverters(DBTypeConverters::class)
@ForeignKey(entity = LibraryItem::class,
        parentColumns = ["dirUri"],
        childColumns = ["parentUri"],
        onDelete = ForeignKey.CASCADE)
data class Chapter(@PrimaryKey val docID: String,
                   val name: String,
                   val mimeType: String,
                   val parentName: String,
                   val parentUri: Uri) {

    var read: Boolean = false
        set
        get
}
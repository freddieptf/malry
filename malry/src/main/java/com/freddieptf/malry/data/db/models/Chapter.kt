package com.freddieptf.malry.data.db.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.freddieptf.malry.data.utils.DBTypeConverters

/**
 * Created by freddieptf on 9/1/18.
 */
@Entity(tableName = "chapters")
@TypeConverters(DBTypeConverters::class)
@ForeignKey(entity = LibraryItem::class,
        parentColumns = ["ID"],
        childColumns = ["parentID"],
        onDelete = ForeignKey.CASCADE)
internal data class Chapter(@PrimaryKey val ID: String,
                            val docID: String?,
                            val name: String,
                            val mimeType: String,
                            val parentID: String) {

    var lastReadPage: Int = 0
        set
        get

    var totalPages: Int = 0
        get
        set

    var lastRead: Long = -1

}
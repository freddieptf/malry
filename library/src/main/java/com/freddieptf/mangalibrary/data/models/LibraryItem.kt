package com.freddieptf.mangalibrary.data.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.freddieptf.mangalibrary.utils.DBTypeConverters

/**
 * Created by freddieptf on 9/17/18.
 */
@Entity(tableName = "library")
@TypeConverters(DBTypeConverters::class)
data class LibraryItem(
        @PrimaryKey val dirUri: Uri,
        val name: String,
        val itemCount: Int,
        val coverImg: String?) {
}
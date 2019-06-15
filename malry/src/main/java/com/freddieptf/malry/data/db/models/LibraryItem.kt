package com.freddieptf.malry.data.db.models

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.freddieptf.malry.data.utils.DBTypeConverters
import com.freddieptf.malry.tachiyomicompat.data.MangaSource

/**
 * Created by freddieptf on 9/17/18.
 */
@Entity(tableName = "library")
@TypeConverters(DBTypeConverters::class)
@ForeignKey(entity = MangaSource::class, parentColumns = ["id"], childColumns = ["sourceID"])
internal data class LibraryItem(
        @PrimaryKey val ID: String,
        val dirUri: Uri,
        val sourceID: Long,
        val title: String,
        val itemCount: Int,
        val coverImg: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readParcelable(Uri::class.java.classLoader),
            parcel.readLong(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ID)
        parcel.writeParcelable(dirUri, flags)
        parcel.writeLong(sourceID)
        parcel.writeString(title)
        parcel.writeInt(itemCount)
        parcel.writeString(coverImg)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LibraryItem> {
        override fun createFromParcel(parcel: Parcel): LibraryItem {
            return LibraryItem(parcel)
        }

        override fun newArray(size: Int): Array<LibraryItem?> {
            return arrayOfNulls(size)
        }
    }

}
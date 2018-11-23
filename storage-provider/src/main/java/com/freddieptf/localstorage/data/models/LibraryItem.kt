package com.freddieptf.localstorage.data.models

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.freddieptf.localstorage.utils.DBTypeConverters

/**
 * Created by freddieptf on 9/17/18.
 */
@Entity(tableName = "library")
@TypeConverters(DBTypeConverters::class)
internal data class LibraryItem(
        @PrimaryKey val dirUri: Uri,
        val name: String,
        val itemCount: Int,
        val coverImg: String?): Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readParcelable(Uri::class.java.classLoader),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(dirUri, flags)
        parcel.writeString(name)
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
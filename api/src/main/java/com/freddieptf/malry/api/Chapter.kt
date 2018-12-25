package com.freddieptf.malry.api

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * Created by freddieptf on 9/22/18.
 */
data class Chapter(val id: String,
                   val title: String,
                   val parentID: String,
                   val parentTitle: String) : Parcelable {

    var lastReadPage: Int = 0
        set
        get

    var totalPages: Int = 0
        set
        get

    var paths: List<String> = ArrayList()
        get

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    fun setPaths(paths: List<String>): Chapter {
        this.paths = paths
        return this
    }

    override fun equals(other: Any?): Boolean {
        return other is Chapter && other.id == id && other.parentID == parentID
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(parentID)
        parcel.writeString(parentTitle)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Chapter> {
        override fun createFromParcel(parcel: Parcel): Chapter {
            return Chapter(parcel)
        }

        override fun newArray(size: Int): Array<Chapter?> {
            return arrayOfNulls(size)
        }
    }
}
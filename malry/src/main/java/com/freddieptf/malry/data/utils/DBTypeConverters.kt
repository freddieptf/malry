package com.freddieptf.malry.data.utils

import android.net.Uri
import androidx.room.TypeConverter

/**
 * Created by freddieptf on 9/22/18.
 */
class DBTypeConverters {

    companion object {

        @JvmStatic
        @TypeConverter
        fun uriToString(uri: Uri): String {
            return uri.toString()
        }

        @JvmStatic
        @TypeConverter
        fun stringToUri(string: String): Uri {
            return Uri.parse(string)
        }

    }

}
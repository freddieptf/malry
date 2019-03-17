package com.freddieptf.malry.ui.library

import android.content.Context
import android.net.Uri
import android.preference.PreferenceManager

/**
 * Created by freddieptf on 9/1/18.
 */
class LibraryPrefs {

    companion object {

        private val LIB_PATHS = "lib_paths"

        fun addLibUri(ctx: Context, uri: Uri) {
            var prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
            var editor = prefs.edit()
            editor.putString(LIB_PATHS, uri.toString())
            editor.apply()
        }

        fun getLibUri(ctx: Context): Uri? {
            var uriString = PreferenceManager.getDefaultSharedPreferences(ctx).getString(LIB_PATHS, "")
            if (uriString.isEmpty()) return null
            return Uri.parse(uriString)
        }

    }


}
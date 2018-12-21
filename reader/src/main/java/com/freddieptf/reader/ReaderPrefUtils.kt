package com.freddieptf.reader

import android.content.Context
import com.freddieptf.reader.utils.ReadMode

/**
 * Created by freddieptf on 10/7/18.
 */
internal class ReaderPrefUtils {

    companion object {

        private val PREFS = "reader_prefs"
        private val READ_DIRECTION_PREF = "read_direction"

        fun setReadDirection(ctx: Context, direction: ReadMode) {
            val direction = if (direction == ReadMode.RIGHT_TO_LEFT) 0 else 1
            val preferences = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            preferences.edit().putInt(READ_DIRECTION_PREF, direction).commit()
        }

        fun getReadDirection(ctx: Context): ReadMode {
            return if (ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(READ_DIRECTION_PREF, -1) == 0)
                ReadMode.RIGHT_TO_LEFT
            else ReadMode.LEFT_TO_RIGHT
        }

    }

}
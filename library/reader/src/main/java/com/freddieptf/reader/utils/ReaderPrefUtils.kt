package com.freddieptf.reader.utils

import android.content.Context
import com.freddieptf.reader.R

/**
 * Created by freddieptf on 10/7/18.
 */
internal class ReaderPrefUtils {

    companion object {

        private val PREFS = "reader_prefs"
        private val READ_DIRECTION_PREF = "read_direction"
        private val PAGE_ASPECT_PREF = "page_aspect"

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

        fun setPageAspectMode(ctx: Context, mode: PageApsectMode) {
            val modeString = if (mode == PageApsectMode.PAGE_FILL) ctx.getString(R.string.page_aspect_fill)
            else ctx.getString(R.string.page_aspect_fit)
            ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .edit()
                    .putString(PAGE_ASPECT_PREF, modeString)
                    .apply()
        }

        fun getPageApsectMode(ctx: Context): PageApsectMode {
            val modeString = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .getString(PAGE_ASPECT_PREF, "")
            when (modeString) {
                ctx.getString(R.string.page_aspect_fill) -> return PageApsectMode.PAGE_FILL
                else -> return PageApsectMode.PAGE_FIT
            }
        }

    }

}
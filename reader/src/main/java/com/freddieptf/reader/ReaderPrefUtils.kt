package com.freddieptf.reader

import android.content.Context
import com.freddieptf.reader.widgets.ReaderViewPager

/**
 * Created by freddieptf on 10/7/18.
 */
internal class ReaderPrefUtils {

    companion object {

        private val PREFS = "reader_prefs"
        private val READ_DIRECTION_PREF = "read_direction"

        fun setReadDirection(ctx: Context, direction: ReaderViewPager.DIRECTION) {
            val direction = if (direction == ReaderViewPager.DIRECTION.RIGHT_TO_LEFT) 0 else 1
            val preferences = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            preferences.edit().putInt(READ_DIRECTION_PREF, direction).apply()
        }

        fun getReadDirection(ctx: Context): ReaderViewPager.DIRECTION {
            return if (ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(READ_DIRECTION_PREF, -1) == 0)
                ReaderViewPager.DIRECTION.RIGHT_TO_LEFT
            else ReaderViewPager.DIRECTION.LEFT_TO_RIGHT
        }

    }

}
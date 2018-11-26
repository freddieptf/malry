package com.freddieptf.malry

import android.content.Context
import android.preference.PreferenceManager

/**
 * Created by freddieptf on 11/26/18.
 */
object PrefUtils {

    private val HAS_SETUP = "has_set_up"

    fun hasSetUp(ctx: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        return sharedPreferences.getBoolean(HAS_SETUP, false)
    }

    fun setFirstSetupComplete(ctx: Context) {
        PreferenceManager.getDefaultSharedPreferences(ctx)
                .edit()
                .putBoolean(HAS_SETUP, true)
                .apply()
    }
}
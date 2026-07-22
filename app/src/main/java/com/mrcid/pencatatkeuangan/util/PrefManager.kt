package com.mrcid.pencatatkeuangan.util

import android.content.Context

class PrefManager(context: Context) {

    private val prefs = context.getSharedPreferences("pencatat_prefs", Context.MODE_PRIVATE)

    var darkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()

    companion object {
        private const val KEY_DARK_MODE = "dark_mode"
    }
}

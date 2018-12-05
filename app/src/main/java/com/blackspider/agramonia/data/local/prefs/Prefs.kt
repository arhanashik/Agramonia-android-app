package com.blackspider.agramonia.data.local.prefs

import android.content.Context
import android.content.SharedPreferences

class Prefs (context: Context) {
    val PREFS_FILENAME = "com.blackspider.agramonia"
    val FARMER_NAME = "farmer_name"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    var name: String
        get() = prefs.getString(FARMER_NAME, "")
        set(value) = prefs.edit().putString(FARMER_NAME, value).apply()
}
package com.workfort.apps.agramoniaapp.data.local.prefs

import android.content.Context
import android.content.SharedPreferences
import com.workfort.apps.agramoniaapp.data.local.constant.Const


object PrefsGlobal: PrefProp{
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(globalPrefName, Context.MODE_PRIVATE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var firstRun: Boolean
        get() = prefs.getBoolean(keyFirstRun, true)
        set(value) = prefs.edit { it.putBoolean(keyFirstRun, value) }

    var selectedLanguageCode: String?
        get() = prefs.getString(keySelectedLanguageCode, Const.LanguageCode.ROMANIAN)
        set(value) = prefs.edit { it.putString(keySelectedLanguageCode, value) }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
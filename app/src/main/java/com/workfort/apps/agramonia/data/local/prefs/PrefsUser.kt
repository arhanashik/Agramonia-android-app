package com.workfort.apps.agramonia.data.local.prefs

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.workfort.apps.agramonia.data.local.farmer.FarmerEntity


object PrefsUser : PrefProp {
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(userPrefName, Context.MODE_PRIVATE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var farmer: FarmerEntity?
        get() {
            val farmerJson = prefs.getString(keyFarmerEntity, "")
            return Gson().fromJson(farmerJson, FarmerEntity::class.java)?: return null
        }
        set(value) = prefs.edit { it.putString(keyFarmerEntity, Gson().toJson(value)) }

    var session: Boolean
        get() = prefs.getBoolean(keySession, false)
        set(value) = prefs.edit { it.putBoolean(keySession, value) }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
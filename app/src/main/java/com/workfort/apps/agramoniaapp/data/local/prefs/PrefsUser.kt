package com.workfort.apps.agramoniaapp.data.local.prefs

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.workfort.apps.agramoniaapp.data.local.farmer.FamilyEntity


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

    var family: FamilyEntity?
        get() {
            val farmerJson = prefs.getString(keyFarmerEntity, "")
            return Gson().fromJson(farmerJson, FamilyEntity::class.java)?: return null
        }
        set(value) = prefs.edit { it.putString(keyFarmerEntity, Gson().toJson(value)) }

    var session: Boolean
        get() = prefs.getBoolean(keySession, false)
        set(value) = prefs.edit { it.putBoolean(keySession, value) }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
package com.workfort.apps.agramonia.data.local.prefs

interface PrefProp {
    val globalPrefName: String
        get() = "com.blackspider.agramonia.pref_global"

    val userPrefName: String
        get() = "com.blackspider.agramonia.pref_user"

    val keyFirstRun: String
        get() = "first_run"

    val keySelectedLanguageCode: String
        get() = "selected_language"

    val keyFarmerEntity: String
        get() = "farmer_entity"

    val keySession: String
        get() = "session_exists"
}
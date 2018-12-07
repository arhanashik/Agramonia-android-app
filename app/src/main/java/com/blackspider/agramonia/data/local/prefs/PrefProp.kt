package com.blackspider.agramonia.data.local.prefs

interface PrefProp {
    val prefName: String
        get() = "com.blackspider.agramonia"

    val keyFarmerEntity: String
        get() = "farmer_entity"

    val keySession: String
        get() = "session_exists"
}
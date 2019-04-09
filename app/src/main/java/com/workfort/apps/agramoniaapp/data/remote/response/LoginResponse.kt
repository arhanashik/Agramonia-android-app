package com.workfort.apps.agramoniaapp.data.remote.response;

import com.google.gson.annotations.SerializedName
import com.workfort.apps.agramoniaapp.data.local.farmer.FamilyEntity

data class LoginResponse (val success: Boolean,
                          val message: String,
                          @SerializedName("user") val family: FamilyEntity)

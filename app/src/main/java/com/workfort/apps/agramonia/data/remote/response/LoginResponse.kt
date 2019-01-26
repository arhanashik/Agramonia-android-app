package com.workfort.apps.agramonia.data.remote.response;

import com.google.gson.annotations.SerializedName
import com.workfort.apps.agramonia.data.local.farmer.FarmerEntity

data class LoginResponse (val success: Boolean,
                          val failure: Boolean,
                          val message: String,
                          @SerializedName("user") val farmer: FarmerEntity)

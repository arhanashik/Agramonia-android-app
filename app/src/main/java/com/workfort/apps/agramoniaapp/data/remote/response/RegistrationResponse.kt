package com.workfort.apps.agramoniaapp.data.remote.response;

import com.google.gson.annotations.SerializedName
import com.workfort.apps.agramoniaapp.data.local.farmer.FarmerEntity

data class RegistrationResponse (val success: Boolean,
                                 val failure: Boolean,
                                 val message: String,
                                 @SerializedName("user") val farmer: FarmerEntity)

package com.blackspider.agramonia.data.remote.response;

import com.blackspider.agramonia.data.local.farmer.FarmerEntity
import com.google.gson.annotations.SerializedName

data class RegistrationResponse (val success: Boolean,
                                 val failure: Boolean,
                                 val message: String,
                                 @SerializedName("user") val farmer: FarmerEntity)

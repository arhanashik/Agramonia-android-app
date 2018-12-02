package com.blackspider.agramonia.data.local.farmer

import com.google.gson.annotations.SerializedName

data class FarmerEntity(val id: Int,
                        val name: String,
                        val location: String,
                        @SerializedName("profile_image") val profileImg: String,
                        val phone: String,
                        val email: String)
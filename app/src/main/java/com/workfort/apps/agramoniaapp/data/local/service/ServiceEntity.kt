package com.workfort.apps.agramoniaapp.data.local.service

import com.google.gson.annotations.SerializedName

data class ServiceEntity (val id: Int,
                          @SerializedName("title_en") val titleEn: String,
                          @SerializedName("title_de") val titleDe: String,
                          @SerializedName("title_rm") val titleRm: String,
                          val price: Int,
                          val image: String?,
                          val available: Int?,
                          @SerializedName(" family_id") val familyId: Int?)
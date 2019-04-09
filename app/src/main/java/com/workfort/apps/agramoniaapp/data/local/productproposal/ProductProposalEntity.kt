package com.workfort.apps.agramoniaapp.data.local.productproposal

import com.google.gson.annotations.SerializedName

data class ProductProposalEntity(val id: Int,
                                 @SerializedName("title") val titleEn: String,
                                 @SerializedName("title_de") val titleDe: String,
                                 @SerializedName("title_rm") val titleRm: String,
                                 @SerializedName("description") val descriptionEn: String,
                                 @SerializedName("description_de") val descriptionDe: String,
                                 @SerializedName("description_rm") val descriptionRm: String,
                                 val price: Int,
                                 @SerializedName("contact_number") val contact: String,
                                 val image: String?,
                                 @SerializedName("farmer_id") val familyId: Int?)
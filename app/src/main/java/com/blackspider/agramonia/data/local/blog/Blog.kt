package com.blackspider.agramonia.data.local.blog

import com.google.gson.annotations.SerializedName

data class Blog(val id: String,
                val title: String,
                val description: String,
                @SerializedName("image_list") val imageList: List<String>)
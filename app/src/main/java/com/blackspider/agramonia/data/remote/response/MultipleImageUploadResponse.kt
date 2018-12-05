package com.blackspider.agramonia.data.remote.response

import com.google.gson.annotations.SerializedName

data class MultipleImageUploadResponse (val isError: Boolean,
                                        val message: String,
                                        val urls: List<String>)
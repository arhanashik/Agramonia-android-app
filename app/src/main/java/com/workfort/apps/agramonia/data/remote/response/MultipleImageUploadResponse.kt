package com.workfort.apps.agramonia.data.remote.response

data class MultipleImageUploadResponse (val isError: Boolean,
                                        val message: String,
                                        val urls: List<String>)
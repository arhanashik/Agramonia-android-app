package com.workfort.apps.agramoniaapp.data.remote.response

data class MultipleImageUploadResponse (val isError: Boolean,
                                        val message: String,
                                        val urls: ArrayList<String>)
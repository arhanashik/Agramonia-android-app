package com.workfort.apps.agramoniaapp.data.remote.response

data class MultipleImageUploadResponse (val error: Boolean,
                                        val message: String,
                                        val urls: ArrayList<String>)
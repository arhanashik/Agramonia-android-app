package com.workfort.apps.agramoniaapp.data.remote.response

import com.google.gson.annotations.SerializedName
import com.workfort.apps.agramoniaapp.data.local.service.ServiceEntity

data class ServiceListResponse(@SerializedName("data") val services: ArrayList<ServiceEntity>)
package com.workfort.apps.agramoniaapp.data.remote.response

import com.workfort.apps.agramoniaapp.data.local.service.ServiceEntity

data class CreateServiceResponse (val success: Boolean,
                                  val message: String,
                                  val service: ServiceEntity)
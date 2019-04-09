package com.workfort.apps.agramoniaapp.data.remote.response

import com.google.gson.annotations.SerializedName
import com.workfort.apps.agramoniaapp.data.local.productproposal.ProductProposalEntity
import com.workfort.apps.agramoniaapp.data.local.service.ServiceEntity

data class ProposalListResponse(@SerializedName("data")
                                val proposals: ArrayList<ProductProposalEntity>)
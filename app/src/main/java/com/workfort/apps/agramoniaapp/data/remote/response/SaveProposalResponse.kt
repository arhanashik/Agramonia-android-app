package com.workfort.apps.agramoniaapp.data.remote.response

import com.workfort.apps.agramoniaapp.data.local.productproposal.ProductProposalEntity

data class SaveProposalResponse (val success: Boolean,
                                 val message: String,
                                 val proposal: ProductProposalEntity?)
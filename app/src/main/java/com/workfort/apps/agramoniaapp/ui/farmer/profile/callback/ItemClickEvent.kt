package com.workfort.apps.agramoniaapp.ui.farmer.profile.callback

import com.workfort.apps.agramoniaapp.data.local.blog.BlogEntity
import com.workfort.apps.agramoniaapp.data.local.productproposal.ProductProposalEntity
import com.workfort.apps.agramoniaapp.data.local.service.ServiceEntity

interface ItemClickEvent {
    fun onClickService(service: ServiceEntity, position: Int)
    fun onClickProposal(proposal: ProductProposalEntity, position: Int)
    fun onClickBlog(blog: BlogEntity, position: Int)
}
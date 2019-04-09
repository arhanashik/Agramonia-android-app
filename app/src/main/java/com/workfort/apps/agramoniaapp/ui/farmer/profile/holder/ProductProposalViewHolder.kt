package com.workfort.apps.agramoniaapp.ui.farmer.profile.holder

import androidx.recyclerview.widget.RecyclerView
import com.workfort.apps.agramoniaapp.data.local.blog.BlogEntity
import com.workfort.apps.agramoniaapp.data.local.productproposal.ProductProposalEntity
import com.workfort.apps.agramoniaapp.databinding.ItemProductProposalBinding
import com.workfort.apps.agramoniaapp.ui.farmer.profile.callback.ItemClickEvent

class ProductProposalViewHolder(private val binding: ItemProductProposalBinding)
    : RecyclerView.ViewHolder(binding.root) {

    fun bind(proposal: ProductProposalEntity, callback: ItemClickEvent?) {
        binding.textViewTitle.text = proposal.titleEn
        binding.textViewDescription.text = proposal.descriptionEn

        binding.cardViewContainer.setOnClickListener {
            callback?.onClickProposal(proposal, adapterPosition)
        }
    }
}
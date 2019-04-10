package com.workfort.apps.agramoniaapp.ui.farmer.profile.holder

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.workfort.apps.agramoniaapp.R
import com.workfort.apps.agramoniaapp.data.local.constant.Const
import com.workfort.apps.agramoniaapp.data.local.productproposal.ProductProposalEntity
import com.workfort.apps.agramoniaapp.databinding.ItemProductProposalBinding
import com.workfort.apps.agramoniaapp.ui.base.callback.ItemClickListener
import com.workfort.apps.agramoniaapp.ui.base.helper.LinearHorizontalMarginItemDecoration
import com.workfort.apps.agramoniaapp.ui.farmer.createblog.PhotoAdapter
import com.workfort.apps.agramoniaapp.ui.farmer.profile.callback.ItemClickEvent
import com.workfort.apps.util.helper.ViewUtils

class ProductProposalViewHolder(private val binding: ItemProductProposalBinding)
    : RecyclerView.ViewHolder(binding.root) {

    fun bind(lang: String, proposal: ProductProposalEntity, callback: ItemClickEvent?) {
        var title = proposal.titleRm
        var description = proposal.descriptionRm
        when(lang) {
            Const.Language.ENGLISH -> {
                title = proposal.titleEn
                description = proposal.descriptionEn
            }
            Const.Language.GERMANY -> {
                title = proposal.titleDe
                description = proposal.descriptionDe
            }
        }
        description += "\nPrice: ${proposal.price}"

        binding.textViewTitle.text = title
        binding.textViewDescription.text = description

        binding.cardViewContainer.setOnClickListener {
            callback?.onClickProposal(proposal, adapterPosition)
        }
    }

    private fun setImages() {
        val photoAdapter = PhotoAdapter()
        ViewUtils.initializeRecyclerView(binding.rvPhotos, photoAdapter, null, null,
                LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false),
                LinearHorizontalMarginItemDecoration(ViewUtils.getPixel(R.dimen.margin_8)),
                DefaultItemAnimator())
    }
}
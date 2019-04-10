package com.workfort.apps.agramoniaapp.ui.farmer.profile.holder

import androidx.recyclerview.widget.RecyclerView
import com.workfort.apps.agramoniaapp.data.local.constant.Const
import com.workfort.apps.agramoniaapp.data.local.service.ServiceEntity
import com.workfort.apps.agramoniaapp.databinding.ItemServiceBinding
import com.workfort.apps.agramoniaapp.ui.farmer.profile.callback.ItemClickEvent

class ServiceViewHolder(private val binding: ItemServiceBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(lang: String, service: ServiceEntity, callback: ItemClickEvent?) {
        val title = when(lang) {
            Const.Language.ENGLISH -> {
                service.titleEn
            }
            Const.Language.GERMANY -> {
                service.titleDe
            }
            else -> service.titleRm
        }

        binding.textViewTitle.text = title
        val description = "Price: ${service.price}"
        binding.textViewDescription.text = description

        binding.cardViewContainer.setOnClickListener {
            callback?.onClickService(service, adapterPosition)
        }
    }
}
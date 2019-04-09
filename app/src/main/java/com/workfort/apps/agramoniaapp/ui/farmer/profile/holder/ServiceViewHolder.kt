package com.workfort.apps.agramoniaapp.ui.farmer.profile.holder

import androidx.recyclerview.widget.RecyclerView
import com.workfort.apps.agramoniaapp.data.local.service.ServiceEntity
import com.workfort.apps.agramoniaapp.databinding.ItemServiceBinding
import com.workfort.apps.agramoniaapp.ui.farmer.profile.callback.ItemClickEvent

class ServiceViewHolder(private val binding: ItemServiceBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(service: ServiceEntity, callback: ItemClickEvent?) {
        binding.textViewTitle.text = service.titleEn
        val description = "Price: ${service.price}"
        binding.textViewDescription.text = description

        binding.cardViewContainer.setOnClickListener {
            callback?.onClickService(service, adapterPosition)
        }
    }
}
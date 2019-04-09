package com.workfort.apps.agramoniaapp.ui.farmer.profile.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.workfort.apps.agramoniaapp.R
import com.workfort.apps.agramoniaapp.databinding.ItemHeaderBinding

class HeaderViewHolder(private val binding: ItemHeaderBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(title: String, shouldHide: Boolean) {
        binding.container.visibility = if(shouldHide) View.GONE else View.VISIBLE
        binding.tvTitle.text = title
    }
}
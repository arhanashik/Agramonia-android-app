package com.workfort.apps.agramoniaapp.ui.farmer.profile.holder

import androidx.recyclerview.widget.RecyclerView
import com.workfort.apps.agramoniaapp.data.local.blog.BlogEntity
import com.workfort.apps.agramoniaapp.databinding.ItemBlogBinding
import com.workfort.apps.agramoniaapp.ui.farmer.profile.callback.ItemClickEvent

class BlogViewHolder(private val binding: ItemBlogBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(blog: BlogEntity, callback: ItemClickEvent?) {
        binding.textViewTitle.text = blog.title
        binding.textViewDescription.text = blog.description

        binding.cardViewContainer.setOnClickListener {
            callback?.onClickBlog(blog, adapterPosition)
        }
    }
}
package com.workfort.apps.agramonia.ui.farmer.profile

import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.workfort.apps.agramonia.R
import com.workfort.apps.agramonia.data.local.blog.BlogEntity
import com.workfort.apps.agramonia.ui.base.component.BaseAdapter
import com.workfort.apps.agramonia.ui.base.component.BaseViewHolder

import com.workfort.apps.agramonia.databinding.ItemBlogBinding

class BlogAdapter : BaseAdapter<BlogEntity>() {
    override fun isEqual(left: BlogEntity, right: BlogEntity): Boolean {
        return left.id == right.id
    }

    override fun newViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<BlogEntity> {
        return BlogViewHolder(inflate(parent, R.layout.item_blog))
    }

    inner class BlogViewHolder(viewDataBinding: ViewDataBinding) : BaseViewHolder<BlogEntity>(viewDataBinding) {

        private val mBinding = viewDataBinding as ItemBlogBinding

        override fun bind(item: BlogEntity) {
            mBinding.textViewTitle.text = item.title
            mBinding.textViewDescription.text = item.description

            setClickListener(mBinding.cardViewContainer)
            mBinding.executePendingBindings()
        }

        override fun onClick(view: View) {
            if (getItem(adapterPosition) != null)
                mItemClickListener?.onItemClick(view, getItem(adapterPosition)!!)
        }
    }
}
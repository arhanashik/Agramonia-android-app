package com.blackspider.agramonia.ui.farmer.profile

import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.blackspider.agramonia.R
import com.blackspider.agramonia.data.local.blog.Blog
import com.blackspider.agramonia.databinding.ItemBlogBinding
import com.blackspider.agramonia.ui.base.component.BaseAdapter
import com.blackspider.agramonia.ui.base.component.BaseViewHolder

class BlogAdapter : BaseAdapter<Blog>() {
    override fun isEqual(left: Blog, right: Blog): Boolean {
        return left.id == right.id
    }

    override fun newViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Blog> {
        return BlogViewHolder(inflate(parent, R.layout.item_blog))
    }

    inner class BlogViewHolder(viewDataBinding: ViewDataBinding) : BaseViewHolder<Blog>(viewDataBinding) {

        private val mBinding = viewDataBinding as ItemBlogBinding

        override fun bind(item: Blog) {
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
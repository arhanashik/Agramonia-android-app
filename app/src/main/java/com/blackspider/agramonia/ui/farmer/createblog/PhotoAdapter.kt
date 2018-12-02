package com.blackspider.agramonia.ui.farmer.createblog

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.blackspider.AgromoniaApplication
import com.blackspider.agramonia.R
import com.blackspider.agramonia.databinding.ItemBlogPhotoBinding
import com.blackspider.agramonia.ui.base.component.BaseAdapter
import com.blackspider.agramonia.ui.base.component.BaseViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target


class PhotoAdapter : BaseAdapter<Uri>() {
    override fun isEqual(left: Uri, right: Uri): Boolean {
        return left == right
    }

    override fun newViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Uri> {
        return PhotoViewHolder(inflate(parent, R.layout.item_blog_photo))
    }

    inner class PhotoViewHolder(viewDataBinding: ViewDataBinding) : BaseViewHolder<Uri>(viewDataBinding) {
        private val mBinding = viewDataBinding as ItemBlogPhotoBinding

        override fun bind(item: Uri) {
            Glide.with(AgromoniaApplication.getBaseApplicationContext())
                    .asDrawable()
                    .load(item)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?,
                                                  target: Target<Drawable>?,
                                                  isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?,
                                                     target: Target<Drawable>?,
                                                     dataSource: DataSource?,
                                                     isFirstResource: Boolean): Boolean {
                            return false
                        }

                    })
                    .into(mBinding.imageViewPhoto)

            setClickListener(mBinding.imageViewCross)
            mBinding.executePendingBindings()
        }

        override fun onClick(view: View) {
            if (getItem(adapterPosition) != null)
                mItemClickListener?.onItemClick(view, getItem(adapterPosition)!!, adapterPosition)
        }
    }
}
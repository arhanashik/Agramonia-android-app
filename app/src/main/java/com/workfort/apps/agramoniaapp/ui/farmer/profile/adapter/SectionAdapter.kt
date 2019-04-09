package com.workfort.apps.agramoniaapp.ui.farmer.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.workfort.apps.agramoniaapp.R
import com.workfort.apps.agramoniaapp.data.local.blog.BlogEntity
import com.workfort.apps.agramoniaapp.data.local.productproposal.ProductProposalEntity
import com.workfort.apps.agramoniaapp.data.local.service.ServiceEntity
import com.workfort.apps.agramoniaapp.ui.farmer.profile.callback.ItemClickEvent
import com.workfort.apps.agramoniaapp.ui.farmer.profile.holder.BlogViewHolder
import com.workfort.apps.agramoniaapp.ui.farmer.profile.holder.HeaderViewHolder
import com.workfort.apps.agramoniaapp.ui.farmer.profile.holder.ProductProposalViewHolder
import com.workfort.apps.agramoniaapp.ui.farmer.profile.holder.ServiceViewHolder

class SectionAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mServices = ArrayList<ServiceEntity>()
    private val mProductProposals = ArrayList<ProductProposalEntity>()
    private val mBolgs = ArrayList<BlogEntity>()

    private var mCallback: ItemClickEvent? = null

    private object ViewType{
        const val HEADER = 0
        const val SERVICE = 1
        const val PROPOSAL = 2
        const val BLOG = 3
    }

    fun setServices(services: ArrayList<ServiceEntity>) {
        mServices.clear()
        mServices.addAll(services)
        notifyDataSetChanged()
    }

    fun addService(service: ServiceEntity) {
        mServices.add(service)
        notifyItemInserted(mServices.size + 1)
    }

    fun setProductProposals(proposals: ArrayList<ProductProposalEntity>) {
        mProductProposals.clear()
        mProductProposals.addAll(proposals)
        notifyDataSetChanged()
    }

    fun addProductProposal(proposal: ProductProposalEntity) {
        mProductProposals.add(proposal)

        var insertPosition = mServices.size + mProductProposals.size + 1
        if(mServices.isNotEmpty()) insertPosition++
        notifyItemInserted(insertPosition)
    }

    fun setBlogs(blogs: ArrayList<BlogEntity>) {
        mBolgs.clear()
        mBolgs.addAll(blogs)
        notifyDataSetChanged()
    }

    fun addBlog(blog: BlogEntity) {
        mBolgs.add(blog)

        var insertPosition = mServices.size + mProductProposals.size + mBolgs.size + 1
        if(mServices.isNotEmpty()) insertPosition++
        if(mProductProposals.isNotEmpty()) insertPosition++
        notifyItemInserted(insertPosition)
    }

    fun setCallback(callback: ItemClickEvent) {
        mCallback = callback
    }

    override fun getItemCount(): Int {
        return mServices.size + mProductProposals.size + mBolgs.size + 3
    }

    override fun getItemViewType(position: Int): Int {
        //1st header
        if(position == 0) return ViewType.HEADER

        val header2 = mServices.size + 1
        val header3 = header2 + mProductProposals.size + 1

        if(position == header2 || position == header3) return ViewType.HEADER

        if(mServices.isNotEmpty() && position in 1..(header2 - 1))
            return ViewType.SERVICE

        if(mProductProposals.isNotEmpty() && position in (header2 + 1)..(header3 - 1))
            return ViewType.PROPOSAL

        return ViewType.BLOG
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            ViewType.SERVICE -> ServiceViewHolder(DataBindingUtil.inflate(
                    inflater, R.layout.item_service, parent, false
            ))
            ViewType.PROPOSAL -> ProductProposalViewHolder(DataBindingUtil.inflate(
                    inflater, R.layout.item_product_proposal, parent, false
            ))
            ViewType.BLOG -> BlogViewHolder(DataBindingUtil.inflate(
                    inflater, R.layout.item_blog, parent, false
            ))
            else -> HeaderViewHolder(DataBindingUtil.inflate(
                    inflater, R.layout.item_header, parent, false
            ))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(position == 0) {
            (holder as HeaderViewHolder).bind("Service", mServices.isEmpty())
            return
        }

        val header2 = mServices.size + 1
        val header3 = header2 + mProductProposals.size + 1

        if(position == header2) {
            (holder as HeaderViewHolder).bind("Product Proposal", mProductProposals.isEmpty())
            return
        }

        if(position == header3) {
            (holder as HeaderViewHolder).bind("Blogs", mBolgs.isEmpty())
            return
        }

        if(position in 1..(header2 - 1) ) {
            (holder as ServiceViewHolder).bind(mServices[position - 1], mCallback)
            return
        }

        if(position in (header2 + 1)..(header3 - 1) ) {
            (holder as ProductProposalViewHolder).bind(mProductProposals[position - header2 - 1], mCallback)
            return
        }

        (holder as BlogViewHolder).bind(mBolgs[position - header3 - 1], mCallback)
    }
}
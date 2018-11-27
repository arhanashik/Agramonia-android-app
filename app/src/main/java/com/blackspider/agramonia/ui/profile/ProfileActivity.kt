package com.blackspider.agramonia.ui.profile

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.blackspider.agramonia.R
import com.blackspider.agramonia.data.model.Blog
import com.blackspider.agramonia.databinding.ActivityProfileBinding
import com.blackspider.agramonia.ui.base.callback.ItemClickListener
import com.blackspider.agramonia.ui.base.component.BaseActivity
import com.blackspider.agramonia.ui.createblog.CreateBlogActivity
import com.blackspider.util.helper.ViewUtils
import io.diaryofrifat.code.basemvp.ui.base.helper.LinearMarginItemDecoration

class ProfileActivity : BaseActivity<ProfileMvpView, ProfilePresenter>() {

    private lateinit var mBinding: ActivityProfileBinding

    override val layoutResourceId: Int
        get() = R.layout.activity_profile

    override fun getActivityPresenter(): ProfilePresenter {
        return ProfilePresenter()
    }

    override fun getToolbarId(): Int? {
        return R.id.toolbar
    }

    override fun startUI() {
        mBinding = viewDataBinding as ActivityProfileBinding
        mBinding.fabCreateBlog.setOnClickListener(this)

        ViewUtils.initializeRecyclerView(mBinding.recyclerViewBlogs,
                BlogAdapter(),
                object : ItemClickListener<Blog> {
                    override fun onItemClick(view: View, item: Blog) {
                        when (view.id) {
                            R.id.card_view_container -> {
                                Toast.makeText(,
                                        "Click on the item",
                                        Toast.LENGTH_SHORT).show()
                            }

                            else -> {

                            }
                        }
                    }
                },
                null,
                LinearLayoutManager(this),
                LinearMarginItemDecoration(ViewUtils.getPixel(R.dimen.margin_8)),
                DefaultItemAnimator())

        for (i in 1..10) {
            getAdapter().addItem(Blog("$i", "Title $i", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", ArrayList()))
        }
    }

    private fun getAdapter(): BlogAdapter {
        return mBinding.recyclerViewBlogs.adapter as BlogAdapter
    }

    override fun stopUI() {

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.fab_create_blog -> startActivity(Intent(this, CreateBlogActivity::class.java))

            else -> {
            }
        }
    }
}
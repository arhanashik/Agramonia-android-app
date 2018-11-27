package com.blackspider.agramonia.ui.profile

import android.content.Intent
import android.view.View
import com.blackspider.agramonia.R
import com.blackspider.agramonia.databinding.ActivityProfileBinding
import com.blackspider.agramonia.ui.base.component.BaseActivity
import com.blackspider.agramonia.ui.createblog.CreateBlogActivity

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
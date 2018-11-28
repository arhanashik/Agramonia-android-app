package com.blackspider.agramonia.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.blackspider.agramonia.R
import com.blackspider.agramonia.data.constant.PreferenceKey
import com.blackspider.agramonia.data.model.Blog
import com.blackspider.agramonia.databinding.ActivityProfileBinding
import com.blackspider.agramonia.databinding.AlertDialogBlogDetailsBinding
import com.blackspider.agramonia.ui.base.callback.ItemClickListener
import com.blackspider.agramonia.ui.base.component.BaseActivity
import com.blackspider.agramonia.ui.createblog.CreateBlogActivity
import com.blackspider.util.helper.SharedPrefUtils
import com.blackspider.util.helper.ViewUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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

        val userName: String = SharedPrefUtils.get(PreferenceKey.USER_NAME, "Dummy Name")!! // Put the default name here
        val userPhotoUrl: String = SharedPrefUtils.get(PreferenceKey.USER_IMAGE, "")!! // Put the default user image here

        mBinding.textViewUserName.text = userName
        Glide.with(this)
                .asDrawable()
                .load(userPhotoUrl)
                .apply(RequestOptions().error(R.drawable.img_farmer)) // Put the avatar here
                .into(mBinding.imageViewUser)

        ViewUtils.initializeRecyclerView(mBinding.recyclerViewBlogs,
                BlogAdapter(),
                object : ItemClickListener<Blog> {
                    override fun onItemClick(view: View, item: Blog) {
                        when (view.id) {
                            R.id.card_view_container -> {
                                val builder = AlertDialog.Builder(this@ProfileActivity)
                                val dialogBinding = AlertDialogBlogDetailsBinding.inflate(layoutInflater)

                                dialogBinding.textViewTitle.text = item.title
                                dialogBinding.textViewDescription.text = item.description

                                builder.setView(dialogBinding.root)
                                builder.setCancelable(true)

                                val dialog = builder.create()
                                dialog.show()

                                dialogBinding.textViewDismiss.setOnClickListener {
                                    dialog.dismiss()
                                }
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
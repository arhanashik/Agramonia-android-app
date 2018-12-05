package com.blackspider.agramonia.ui.farmer.profile

import android.app.AlertDialog
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.blackspider.agramonia.R
import com.blackspider.agramonia.data.local.blog.Blog
import com.blackspider.agramonia.data.local.farmer.FarmerEntity
import com.blackspider.agramonia.databinding.ActivityProfileBinding
import com.blackspider.agramonia.databinding.AlertDialogBlogDetailsBinding
import com.blackspider.agramonia.ui.base.callback.ItemClickListener
import com.blackspider.agramonia.ui.base.component.BaseActivity
import com.blackspider.agramonia.ui.farmer.createblog.CreateBlogActivity
import com.blackspider.util.helper.ImageLoader
import com.blackspider.util.helper.ViewUtils
import com.blackspider.util.lib.remote.ApiService
import io.diaryofrifat.code.basemvp.ui.base.helper.LinearMarginItemDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ProfileActivity : BaseActivity<ProfileMvpView, ProfilePresenter>() {

    private lateinit var mBinding: ActivityProfileBinding
    private lateinit var farmer: FarmerEntity

    private var disposable: Disposable? = null
    private val apiService by lazy {
        ApiService.create()
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_profile

    override fun getActivityPresenter(): ProfilePresenter {
        return ProfilePresenter()
    }

    override fun getToolbarId(): Int? {
        return R.id.toolbar
    }

    override fun startUI() {
        farmer = intent.getParcelableExtra("FARMER")

        if(TextUtils.isEmpty(farmer.name)) {
            showToast("Invalid farmer")
            finish()
        }
        Timber.d(farmer.profileImg)

        mBinding = viewDataBinding as ActivityProfileBinding
        mBinding.fabCreateBlog.setOnClickListener(this)

        mBinding.textViewUserName.text = farmer.name
        ImageLoader.load(this, mBinding.imageViewUser, farmer.profileImg)

        ViewUtils.initializeRecyclerView(mBinding.recyclerViewBlogs,
                BlogAdapter(),
                object : ItemClickListener<Blog> {
                    override fun onItemClick(view: View, item: Blog) {
                        when (view.id) {
                            R.id.card_view_container -> {
                                showDetails(item)
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

        loadBlogList()
    }

    private fun getAdapter(): BlogAdapter {
        return mBinding.recyclerViewBlogs.adapter as BlogAdapter
    }

    override fun stopUI() {

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.fab_create_blog -> {
                val intent = Intent(this, CreateBlogActivity::class.java)
                intent.putExtra("FARMER_ID", farmer.id)
                startActivity(intent)
            }

            else -> {
            }
        }
    }

    private fun loadBlogList(){
        disposable = apiService.getBlogList(farmer.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    response ->
                    getAdapter().addItems(response)
                }, {
                    error ->
                    Timber.e(error)
                    showToast(error.message.toString())
                })
    }

    private fun showDetails(item: Blog) {
        val builder = AlertDialog.Builder(this)
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

    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}
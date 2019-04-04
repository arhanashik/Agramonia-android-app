package com.workfort.apps.agramoniaapp.ui.farmer.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.workfort.apps.agramoniaapp.R
import com.workfort.apps.agramoniaapp.data.local.blog.BlogEntity
import com.workfort.apps.agramoniaapp.data.local.constant.Const
import com.workfort.apps.agramoniaapp.data.local.farmer.FarmerEntity
import com.workfort.apps.agramoniaapp.data.local.prefs.PrefsUser
import com.workfort.apps.agramoniaapp.databinding.ActivityProfileBinding
import com.workfort.apps.agramoniaapp.databinding.AlertDialogBlogDetailsBinding
import com.workfort.apps.agramoniaapp.databinding.PromptCreateServiceBinding
import com.workfort.apps.agramoniaapp.ui.base.callback.ItemClickListener
import com.workfort.apps.agramoniaapp.ui.base.component.BaseActivity
import com.workfort.apps.agramoniaapp.ui.base.helper.LinearMarginItemDecoration
import com.workfort.apps.agramoniaapp.ui.farmer.barcodegenerator.BarcodeGeneratorActivity
import com.workfort.apps.agramoniaapp.ui.farmer.createblog.CreateBlogActivity
import com.workfort.apps.util.helper.ImageLoader
import com.workfort.apps.util.helper.ViewUtils
import com.workfort.apps.util.lib.remote.ApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ProfileActivity : BaseActivity<ProfileMvpView, ProfilePresenter>() {

    private lateinit var mBinding: ActivityProfileBinding

    private var farmer: FarmerEntity? = null

    private var disposable: CompositeDisposable = CompositeDisposable()
    private val apiService by lazy {
        ApiClient.create()
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_profile

    override fun getActivityPresenter(): ProfilePresenter {
        return ProfilePresenter()
    }

    override fun getToolbarId(): Int? {
        return R.id.toolbar
    }

    override fun getMenuId(): Int {
        return R.menu.menu_profile
    }

    override fun startUI() {
        farmer = PrefsUser.farmer

        if(farmer == null) {
            showToast("Invalid farmer")
            finish()
        }

        Timber.d(farmer?.profileImg)

        mBinding = viewDataBinding as ActivityProfileBinding
        mBinding.fabCreate.setOnClickListener(this)

        mBinding.textViewUserName.text = farmer?.name
        ImageLoader.load(this, mBinding.imageViewUser, farmer?.profileImg)

        ViewUtils.initializeRecyclerView(mBinding.recyclerViewBlogs, BlogAdapter(),
                object : ItemClickListener<BlogEntity> {
                override fun onItemClick(view: View, item: BlogEntity) {
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
            DefaultItemAnimator()
        )

        loadBlogList()
    }

    private fun getAdapter(): BlogAdapter {
        return mBinding.recyclerViewBlogs.adapter as BlogAdapter
    }

    override fun stopUI() {

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.fab_create -> {
                showCreateOptionPopup()
            }

            else -> {
            }
        }
    }

    private fun showCreateOptionPopup() {
        val popupMenu = PopupMenu(this, mBinding.fabCreate)
        popupMenu.inflate(R.menu.menu_create_options)

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.action_create_service -> {
                    showCreateServiceDialog()
                }
                R.id.action_create_blog -> {
                    val intent = Intent(this, CreateBlogActivity::class.java)
                    intent.putExtra(Const.Key.FARMER_ID, farmer?.id)
                    startActivityForResult(intent, Const.RequestCode.CREATE_BLOG)
                }
            }
            true
        }

        popupMenu.show()
    }

    private fun showCreateServiceDialog() {
        val binding = DataBindingUtil.inflate<PromptCreateServiceBinding>(
                layoutInflater, R.layout.prompt_create_service, null, false
        )

        val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.label_create_service)
                .setView(binding.root)
                .create()

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val price = binding.etPrice.text.toString()

            if(TextUtils.isEmpty(title) || TextUtils.isEmpty(price)) return@setOnClickListener

            binding.btnSave.isEnabled = false
            disposable.add(apiService.createService(title, price.toInt(), farmer?.id!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        binding.pb.visibility = View.INVISIBLE
                        binding.btnSave.isEnabled = true
                        showToast(it.message)

                        if(it.success) dialog.dismiss()
                    }, {
                        Timber.e(it)
                        binding.pb.visibility = View.INVISIBLE
                        binding.btnSave.isEnabled = true
                        showToast(it.message.toString())
                    })
            )
        }

        dialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_logout -> {
                val dialog = AlertDialog.Builder(this)
                        .setMessage(getString(R.string.logout_confirmation))
                        .setPositiveButton(getString(R.string.label_yes))
                        {
                            dialog, _ ->
                            dialog.dismiss()
                            PrefsUser.clear()
                            finish()
                        }
                        .setNegativeButton(getString(R.string.label_cancel))
                        {
                            dialog, _->
                            dialog.dismiss()
                        }
                        .create()

                dialog.show()
            }
            R.id.action_barcode -> {
                startActivity(Intent(this, BarcodeGeneratorActivity::class.java))
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == Const.RequestCode.CREATE_BLOG
                && resultCode == Activity.RESULT_OK) {
            loadBlogList()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadBlogList(){
        mBinding.pb.visibility = View.VISIBLE
        mBinding.textViewNoData.visibility = View.INVISIBLE
        mBinding.recyclerViewBlogs.visibility = View.INVISIBLE

        disposable.add(apiService.getBlogList(farmer!!.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    response ->
                    mBinding.pb.visibility = View.INVISIBLE
                    if(response.isNotEmpty()) {
                        mBinding.recyclerViewBlogs.visibility = View.VISIBLE
                        getAdapter().addItems(response)
                    }
                    else mBinding.textViewNoData.visibility = View.VISIBLE
                }, {
                    error ->
                    Timber.e(error)
                    mBinding.pb.visibility = View.INVISIBLE
                    mBinding.textViewNoData.visibility = View.VISIBLE
                    showToast(error.message.toString())
                })
        )
    }

    private fun showDetails(item: BlogEntity) {
        val builder = AlertDialog.Builder(this)
        val dialogBinding = AlertDialogBlogDetailsBinding.inflate(layoutInflater)

        dialogBinding.textViewTitle.text = item.title
        val description = item.description + "\n\nTotal images: " + item.imageList?.size
        dialogBinding.textViewDescription.text = description

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
        disposable.dispose()
        super.onDestroy()
    }
}
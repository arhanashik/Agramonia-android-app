package com.workfort.apps.agramoniaapp.ui.farmer.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.workfort.apps.agramoniaapp.R
import com.workfort.apps.agramoniaapp.data.local.blog.BlogEntity
import com.workfort.apps.agramoniaapp.data.local.constant.Const
import com.workfort.apps.agramoniaapp.data.local.farmer.FamilyEntity
import com.workfort.apps.agramoniaapp.data.local.prefs.PrefsUser
import com.workfort.apps.agramoniaapp.data.local.productproposal.ProductProposalEntity
import com.workfort.apps.agramoniaapp.data.local.service.ServiceEntity
import com.workfort.apps.agramoniaapp.databinding.ActivityProfileBinding
import com.workfort.apps.agramoniaapp.databinding.AlertDialogBlogDetailsBinding
import com.workfort.apps.agramoniaapp.databinding.PromptCreateProposalBinding
import com.workfort.apps.agramoniaapp.databinding.PromptCreateServiceBinding
import com.workfort.apps.agramoniaapp.ui.base.callback.ItemClickListener
import com.workfort.apps.agramoniaapp.ui.base.component.BaseActivity
import com.workfort.apps.agramoniaapp.ui.base.helper.LinearHorizontalMarginItemDecoration
import com.workfort.apps.agramoniaapp.ui.farmer.barcodegenerator.BarcodeGeneratorActivity
import com.workfort.apps.agramoniaapp.ui.farmer.createblog.CreateBlogActivity
import com.workfort.apps.agramoniaapp.ui.farmer.createblog.PhotoAdapter
import com.workfort.apps.agramoniaapp.ui.farmer.profile.adapter.SectionAdapter
import com.workfort.apps.agramoniaapp.ui.farmer.profile.callback.ItemClickEvent
import com.workfort.apps.util.helper.*
import com.workfort.apps.util.lib.remote.ApiClient
import id.zelory.compressor.Compressor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONArray
import timber.log.Timber
import java.io.File

class ProfileActivity : BaseActivity<ProfileMvpView, ProfilePresenter>() {

    private lateinit var mBinding: ActivityProfileBinding

    private var mAdapter: SectionAdapter? = null
    private var family: FamilyEntity? = null

    private var photoAdapter = PhotoAdapter()

    private var disposable: CompositeDisposable = CompositeDisposable()
    private val apiService by lazy { ApiClient.create() }

    private var promptCreateServiceBinding: PromptCreateServiceBinding? = null
    private var serviceImageInfo: ImageInfo? = null

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
        family = PrefsUser.family

        if(family == null) {
            Toaster(this).showToast(R.string.invalid_family)
            finish()
        }

        Timber.d(family?.profileImg)

        mBinding = viewDataBinding as ActivityProfileBinding
        mBinding.fabCreate.setOnClickListener(this)

        mBinding.textViewUserName.text = family?.name
        mBinding.imageViewUser.load(family?.profileImg)

        mAdapter = SectionAdapter()
        mAdapter?.setCallback(object: ItemClickEvent{
            override fun onClickService(service: ServiceEntity, position: Int) {

            }

            override fun onClickProposal(proposal: ProductProposalEntity, position: Int) {

            }

            override fun onClickBlog(blog: BlogEntity, position: Int) {
                showBlogDetails(blog)
            }
        })

        mBinding.recyclerViewBlogs.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerViewBlogs.itemAnimator = DefaultItemAnimator()
        mBinding.recyclerViewBlogs.adapter = mAdapter

        loadServiceList()
        loadProposalList()
        loadBlogList()
    }

    override fun stopUI() {

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.fab_create -> {
                showCreateOptionPopup()
            }
            R.id.tv_choose_img_service,
            R.id.img_service -> onClickPickServiceImage()
            else -> { }
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
                    intent.putExtra(Const.Key.FARMER_ID, family?.id)
                    startActivityForResult(intent, Const.RequestCode.CREATE_BLOG)
                }
                R.id.action_create_proposal -> {
                    showCreateProposalDialog()
                }
            }
            true
        }

        popupMenu.show()
    }

    private fun onClickPickServiceImage() {
        if (PermissionUtil.on().request(this,
                        PermissionUtil.REQUEST_CODE_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            ImagePicker.pickImage(this, Const.RequestCode.PIC_SERVICE_PHOTO)
        }
    }

    private fun showCreateServiceDialog() {
        val binding = DataBindingUtil.inflate<PromptCreateServiceBinding>(
                layoutInflater, R.layout.prompt_create_service, null, false
        )

        promptCreateServiceBinding = binding
        val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.label_create_service)
                .setView(binding.root)
                .create()

        setClickListener(binding.tvChooseImgService, binding.imgService)
        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val price = binding.etPrice.text.toString()

            if(TextUtils.isEmpty(title) || TextUtils.isEmpty(price)) return@setOnClickListener
            if(serviceImageInfo == null) {
                Toaster(this).showToast(R.string.image_required_exception)
                return@setOnClickListener
            }

            binding.btnSave.isEnabled = false
            binding.pb.visibility = View.VISIBLE
            val prefix = RequestBody.create(
                    MediaType.parse("text/plain"), Const.Prefix.SERVICE
            )

            val serviceImageMultipart = ImageUtil.getMultiPartBody(
                    serviceImageInfo?.imageUri!!
            )
            disposable.add(apiService.uploadImage(prefix, serviceImageMultipart)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ imageUploadResponse ->
                        if(imageUploadResponse.error) {
                            binding.pb.visibility = View.INVISIBLE
                            binding.btnSave.isEnabled = true
                            Toaster(this).showToast(imageUploadResponse.message)
                            return@subscribe
                        }
                        disposable.add(apiService.saveService(title, title, title,
                                price.toInt(), imageUploadResponse.url, family?.id!!)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    binding.pb.visibility = View.INVISIBLE
                                    binding.btnSave.isEnabled = true
                                    Toaster(this).showToast(it.message)

                                    if(it.success) {
                                        dialog.dismiss()
                                        mAdapter?.addService(it.service!!)
                                    }
                                }, {
                                    Timber.e(it)
                                    binding.pb.visibility = View.INVISIBLE
                                    binding.btnSave.isEnabled = true
                                    Toaster(this).showToast(R.string.unknown_exception)
                                })
                        )
                    }, {
                        Timber.e(it)
                        binding.pb.visibility = View.INVISIBLE
                        binding.btnSave.isEnabled = true
                        Toaster(this).showToast(R.string.unknown_exception)
                    })
            )
        }

        dialog.show()
    }

    private fun showCreateProposalDialog() {
        val binding = DataBindingUtil.inflate<PromptCreateProposalBinding>(
                layoutInflater, R.layout.prompt_create_proposal, null, false
        )

        initRecyclerView(binding.rvPhotos)
        binding.btnAddPhoto.setOnClickListener {
            if(photoAdapter.itemCount == 3) {
                Toaster(this).showToast(R.string.max_photo_added)
            } else onClickPickProposalImage()
        }

        val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.label_create_proposal)
                .setView(binding.root)
                .create()

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val description = binding.etDescription.text.toString()
            val price = binding.etPrice.text.toString()

            if(TextUtils.isEmpty(title)
                    || TextUtils.isEmpty(description)
                    || TextUtils.isEmpty(price)) return@setOnClickListener

            if(photoAdapter.itemCount == 0) {
                Toaster(this).showToast(R.string.image_required_exception)
                return@setOnClickListener
            }

            binding.btnSave.isEnabled = false
            binding.btnAddPhoto.isEnabled = false
            binding.pb.visibility = View.VISIBLE
            val multipartBodyParts = ImageUtil.getMultiPartBody(
                    photoAdapter.getItems())

            val prefix = RequestBody.create(
                    MediaType.parse("text/plain"), Const.Prefix.PROPOSAL
            )
            disposable.add(apiService.uploadMultipleImage(prefix, multipartBodyParts)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        response ->
                        val imgArr = JSONArray()
                        response.urls.forEach { img -> imgArr.put(img) }

                        disposable.add(apiService.saveProposal(title, title, title,
                                description, description, description, price.toInt(),
                                family?.phone.toString(), imgArr.toString(), family?.id!!)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    binding.pb.visibility = View.INVISIBLE
                                    binding.btnSave.isEnabled = true
                                    binding.btnAddPhoto.isEnabled = true
                                    Toaster(this).showToast(it.message)

                                    if(it.success) {
                                        dialog.dismiss()
                                        mAdapter?.addProductProposal(it.proposal!!)
                                    }
                                }, {
                                    Timber.e(it)
                                    binding.pb.visibility = View.INVISIBLE
                                    binding.btnSave.isEnabled = true
                                    binding.btnAddPhoto.isEnabled = true
                                    Toaster(this).showToast(R.string.unknown_exception)
                                })
                        )
                    }, {
                        Timber.e(it)
                        binding.pb.visibility = View.INVISIBLE
                        binding.btnSave.isEnabled = true
                        binding.btnAddPhoto.isEnabled = true
                        Toaster(this).showToast(R.string.unknown_exception)
                    })
            )
        }

        dialog.show()
    }

    private fun initRecyclerView(rv: RecyclerView) {
        photoAdapter = PhotoAdapter()
        ViewUtils.initializeRecyclerView(rv, photoAdapter, object : ItemClickListener<Uri> {
            override fun onItemClick(view: View, item: Uri, position: Int) {
                    when (view.id) {
                        R.id.image_view_cross -> {
                            (rv.adapter as PhotoAdapter).removeItem(item)
                        }
                    }
                }
        }, null,
        LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false),
        LinearHorizontalMarginItemDecoration(ViewUtils.getPixel(R.dimen.margin_8)),
        DefaultItemAnimator())
    }

    private fun onClickPickProposalImage() {
        if (PermissionUtil.on().request(this,
                        PermissionUtil.REQUEST_CODE_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            ImagePicker.pickImage(this, Const.RequestCode.PIC_PROPOSAL_PHOTO)
        }
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

        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == Const.RequestCode.CREATE_BLOG) {
                loadBlogList()
            } else {
                val pickedImageInfo = ImagePicker.getPickedImageInfo(
                        this, resultCode, data
                )

                if(pickedImageInfo != null && pickedImageInfo.imageUri != null) {
                    when (requestCode) {
                        Const.RequestCode.PIC_SERVICE_PHOTO -> {
                            serviceImageInfo = pickedImageInfo
                            promptCreateServiceBinding?.tvChooseImgService
                                    ?.visibility = View.INVISIBLE
                            promptCreateServiceBinding?.imgService?.visibility = View.VISIBLE
                            promptCreateServiceBinding?.imgService?.load(
                                    pickedImageInfo.imageUri.toString()
                            )
                        }
                        Const.RequestCode.PIC_PROPOSAL_PHOTO -> {
                            photoAdapter.addItem(pickedImageInfo.imageUri)
                        }
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadServiceList(){
        mBinding.pb.visibility = View.VISIBLE
        mBinding.textViewNoData.visibility = View.INVISIBLE
        mBinding.recyclerViewBlogs.visibility = View.INVISIBLE

        disposable.add(apiService.getServiceList(family!!.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    response ->
                    mBinding.pb.visibility = View.INVISIBLE
                    if(response.services.isNullOrEmpty()) {
                        mBinding.textViewNoData.visibility = View.VISIBLE
                    }else {
                        mBinding.recyclerViewBlogs.visibility = View.VISIBLE
                        mAdapter?.setServices(response.services)
                    }
                }, {
                    error ->
                    Timber.e(error)
                    mBinding.pb.visibility = View.INVISIBLE
                    mBinding.textViewNoData.visibility = View.VISIBLE
                    Toaster(this).showToast(R.string.unknown_exception)
                })
        )
    }

    private fun loadProposalList(){
        mBinding.pb.visibility = View.VISIBLE
        mBinding.textViewNoData.visibility = View.INVISIBLE
        mBinding.recyclerViewBlogs.visibility = View.INVISIBLE

        disposable.add(apiService.getProposalList(family!!.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    response ->
                    mBinding.pb.visibility = View.INVISIBLE
                    if(response.proposals.isNullOrEmpty()) {
                        mBinding.textViewNoData.visibility = View.VISIBLE
                    }else {
                        mBinding.recyclerViewBlogs.visibility = View.VISIBLE
                        mAdapter?.setProductProposals(response.proposals)
                    }
                }, {
                    error ->
                    Timber.e(error)
                    mBinding.pb.visibility = View.INVISIBLE
                    mBinding.textViewNoData.visibility = View.VISIBLE
                    Toaster(this).showToast(R.string.unknown_exception)
                })
        )
    }

    private fun loadBlogList(){
        mBinding.pb.visibility = View.VISIBLE
        mBinding.textViewNoData.visibility = View.INVISIBLE
        mBinding.recyclerViewBlogs.visibility = View.INVISIBLE

        disposable.add(apiService.getBlogList(family!!.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    response ->
                    mBinding.pb.visibility = View.INVISIBLE
                    if(response.isNotEmpty()) {
                        mBinding.recyclerViewBlogs.visibility = View.VISIBLE
                        mAdapter?.setBlogs(ArrayList(response))
                    }
                    else mBinding.textViewNoData.visibility = View.VISIBLE
                }, {
                    error ->
                    Timber.e(error)
                    mBinding.pb.visibility = View.INVISIBLE
                    mBinding.textViewNoData.visibility = View.VISIBLE
                    Toaster(this).showToast(R.string.unknown_exception)
                })
        )
    }

    private fun showBlogDetails(item: BlogEntity) {
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

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
}
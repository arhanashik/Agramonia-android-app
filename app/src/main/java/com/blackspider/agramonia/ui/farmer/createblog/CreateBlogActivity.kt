package com.blackspider.agramonia.ui.farmer.createblog

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.blackspider.agramonia.R
import com.blackspider.agramonia.databinding.ActivityCreateBlogBinding
import com.blackspider.agramonia.ui.base.callback.ItemClickListener
import com.blackspider.agramonia.ui.base.component.BaseActivity
import com.blackspider.agramonia.ui.base.helper.LinearHorizontalMarginItemDecoration
import com.blackspider.util.helper.ImagePicker
import com.blackspider.util.helper.ImageUtil
import com.blackspider.util.helper.PermissionUtil
import com.blackspider.util.helper.ViewUtils
import com.blackspider.util.lib.remote.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File

class CreateBlogActivity : BaseActivity<CreateBlogMvpView, CreateBlogPresenter>() {
    private lateinit var mBinding: ActivityCreateBlogBinding

    private var farmerId = -1

    private var disposable: Disposable? = null
    private val apiService by lazy {
        ApiService.create()
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_create_blog

    override fun getToolbarId(): Int? {
        return R.id.toolbar
    }

    override fun getActivityPresenter(): CreateBlogPresenter {
        return CreateBlogPresenter()
    }

    override fun startUI() {
        farmerId = intent.getIntExtra("FARMER_ID", -1)
        if(farmerId == -1) {
            showToast("Invalid farmer id")
            finish()
        }

        mBinding = viewDataBinding as ActivityCreateBlogBinding
        setTitle(getString(R.string.create_blog))
        mBinding.textViewCreate.setOnClickListener(this)
        mBinding.textViewAddPhoto.setOnClickListener(this)

        ViewUtils.initializeRecyclerView(mBinding.recyclerViewPhotos, PhotoAdapter(),
                object : ItemClickListener<Uri> {
                    override fun onItemClick(view: View, item: Uri, position: Int) {
                        when (view.id) {
                            R.id.image_view_cross -> {
                                getAdapter().removeItem(item)
                                enableAddPhotoButton(getAdapter().itemCount)
                            }

                            else -> {

                            }
                        }
                    }
                }, null,
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false),
                LinearHorizontalMarginItemDecoration(ViewUtils.getPixel(R.dimen.margin_8)),
                DefaultItemAnimator())
    }

    fun enableAddPhotoButton(adapterSize: Int) {
        val txt = "Add photos ($adapterSize/5)"
        mBinding.textViewAddPhoto.text = txt
        val enableIt = adapterSize < 5
        mBinding.textViewAddPhoto.isEnabled = enableIt
    }

    fun getAdapter(): PhotoAdapter {
        return mBinding.recyclerViewPhotos.adapter as PhotoAdapter
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.text_view_create -> {
                val title = mBinding.editTextTitle.text.toString()
                val description = mBinding.editTextDescription.text.toString()

                if(TextUtils.isEmpty(title)){
                    showToast(getString(R.string.title_required_exception))
                    return
                }

                if(TextUtils.isEmpty(description)) {
                    showToast(getString(R.string.description_required_exception))
                    return
                }

                createBlog(title, description)
            }

            R.id.text_view_add_photo -> pickImage()

            else -> {
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PermissionUtil.REQUEST_CODE_STORAGE) {
            for (i in permissions.indices) {
                val permission = permissions[i]

                when (permission) {
                    Manifest.permission.CAMERA -> {
                    }

                    Manifest.permission.READ_EXTERNAL_STORAGE -> {
                    }

                    Manifest.permission.WRITE_EXTERNAL_STORAGE -> if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        pickImage()
                    } else {
                        Toast.makeText(this, "Permission is denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun stopUI() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            ImagePicker.REQUEST_CODE_PICK_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val pickedImageInfo = ImagePicker.getPickedImageInfo(this, resultCode, data)

                    if (pickedImageInfo.imageUri != null) {
                        getAdapter().addItem(pickedImageInfo.imageUri)
                        enableAddPhotoButton(getAdapter().itemCount)
                    }
                } else {
                    if (resultCode != Activity.RESULT_CANCELED) {
                        Toast.makeText(this, "Could not pick image", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            else -> {

            }
        }
    }

    private fun createBlog(title: String, description: String){
        val uriList = getAdapter().getItems()
        if(uriList.size > 0){
            val multipartBodyParts = ArrayList<MultipartBody.Part>()
            var i = 0
            uriList.forEach {
                val path = ImageUtil.getPath(this, it)
                val mediaTypeStr = contentResolver.getType(it)

                if (mediaTypeStr != null) {
                    val mediaType = MediaType.parse(mediaTypeStr)
                    val requestBody = RequestBody.create(mediaType, File(path))
                    val multipartBody = MultipartBody.Part.createFormData("files[$i]",
                            path, requestBody)
                    multipartBodyParts.add(multipartBody)
                    i++
                }
            }

            showToast("Uploading images...")
            disposable = apiService.uploadMultipleImage(multipartBodyParts)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        response1 ->
                        showToast(response1.message)

                        showToast("Creating blog...")
                        disposable = apiService.createBlog(title, description,
                                farmerId, response1.urls)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    response2 ->
                                    showToast(response2.message)
                                }, {
                                    error ->
                                    Timber.e(error)
                                    showToast(error.message.toString())
                                })
                    }, {
                        error ->
                        Timber.e(error)
                        showToast(error.message.toString())
                    })
        }else {
            showToast("Creating blog...")
            disposable = apiService.createBlog(title, description,
                    farmerId, ArrayList<String>())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        response ->
                        showToast(response.message)
                    }, {
                        error ->
                        Timber.e(error)
                        showToast(error.message.toString())
                    })
        }
    }

    private fun pickImage() {
        if (PermissionUtil.on().request(this,
                        PermissionUtil.REQUEST_CODE_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            ImagePicker.pickImage(this)
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
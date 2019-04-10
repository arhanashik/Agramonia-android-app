package com.workfort.apps.agramoniaapp.ui.farmer.createblog

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.workfort.apps.agramoniaapp.R
import com.workfort.apps.agramoniaapp.data.local.constant.Const
import com.workfort.apps.agramoniaapp.ui.base.callback.ItemClickListener
import com.workfort.apps.agramoniaapp.ui.base.component.BaseActivity
import com.workfort.apps.agramoniaapp.ui.base.helper.LinearHorizontalMarginItemDecoration
import com.workfort.apps.util.helper.ImagePicker
import com.workfort.apps.util.helper.ImageUtil
import com.workfort.apps.util.helper.PermissionUtil
import com.workfort.apps.util.helper.ViewUtils
import com.workfort.apps.util.lib.remote.ApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File

import com.workfort.apps.agramoniaapp.databinding.ActivityCreateBlogBinding

class CreateBlogActivity : BaseActivity<CreateBlogMvpView, CreateBlogPresenter>() {
    private lateinit var mBinding: ActivityCreateBlogBinding

    private var farmerId = -1

    private var disposable: Disposable? = null
    private val apiService by lazy {
        ApiClient.create()
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
        farmerId = intent.getIntExtra(Const.Key.FARMER_ID, -1)
        if(farmerId == -1) {
            showToast("Invalid family id")
            finish()
        }

        mBinding = viewDataBinding as ActivityCreateBlogBinding
        setTitle(getString(R.string.label_create_blog))
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

                val dialog = AlertDialog.Builder(this)
                        .setMessage(getString(R.string.create_blog_confirmation))
                        .setPositiveButton(getString(R.string.label_create))
                        {
                            dialog, _ ->
                            dialog.dismiss()
                            createBlog(title, description)
                        }
                        .setNegativeButton(getString(R.string.label_cancel))
                        {
                            dialog, _->
                            dialog.dismiss()
                        }
                        .create()

                dialog.show()
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
        blockUi(true)

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

            val prefix = RequestBody.create(
                    MediaType.parse("text/plain"), Const.Prefix.BLOG
            )
            showToast("Uploading ${multipartBodyParts.size} images...")
            disposable = apiService.uploadMultipleImage(prefix, multipartBodyParts)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        response1 ->
                        showToast(response1.message + ". Creating blog...")

                        Timber.d("${response1.urls.size} urls: %s", response1.urls[0])
                        disposable = apiService.createBlog(title, description,
                                farmerId, response1.urls)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    response2 ->
                                    showToast(response2.message)
                                    val replyIntent = Intent()
                                    replyIntent.putExtra(Const.Key.BLOG, response2.blog)
                                    setResult(Activity.RESULT_OK, replyIntent)
                                    finish()
                                }, {
                                    error ->
                                    blockUi(false)
                                    Timber.e(error)
                                    showToast(error.message.toString())
                                })
                    }, {
                        error ->
                        blockUi(false)
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
                        if(response.success) {
                            val replyIntent = Intent()
                            replyIntent.putExtra(Const.Key.BLOG, response.blog)
                            setResult(Activity.RESULT_OK, replyIntent)
                            finish()
                        }
                    }, {
                        error ->
                        blockUi(false)
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

    private fun blockUi(block: Boolean){
        mBinding.editTextTitle.isEnabled = !block
        mBinding.editTextDescription.isEnabled = !block
        mBinding.textViewAddPhoto.isEnabled = !block
        if(!block) enableAddPhotoButton(getAdapter().itemCount)
        mBinding.pb.visibility = if(block) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}
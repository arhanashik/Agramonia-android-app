package com.blackspider.agramonia.ui.createblog

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import android.widget.Toast
import com.blackspider.agramonia.R
import com.blackspider.agramonia.databinding.ActivityCreateBlogBinding
import com.blackspider.agramonia.ui.base.component.BaseActivity
import com.blackspider.util.helper.ImagePicker
import com.blackspider.util.helper.PermissionUtil

class CreateBlogActivity : BaseActivity<CreateBlogMvpView, CreateBlogPresenter>() {
    private lateinit var mBinding: ActivityCreateBlogBinding

    override val layoutResourceId: Int
        get() = R.layout.activity_create_blog

    override fun getToolbarId(): Int? {
        return R.id.toolbar
    }

    override fun getActivityPresenter(): CreateBlogPresenter {
        return CreateBlogPresenter()
    }

    override fun startUI() {
        mBinding = viewDataBinding as ActivityCreateBlogBinding
        setTitle(getString(R.string.create_blog))
        mBinding.textViewCreate.setOnClickListener(this)
        mBinding.textViewAddPhoto.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.text_view_create -> onBackPressed()

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

    private fun pickImage() {
        if (PermissionUtil.on().request(this,
                        PermissionUtil.REQUEST_CODE_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            ImagePicker.pickImage(this)
        }
    }
}
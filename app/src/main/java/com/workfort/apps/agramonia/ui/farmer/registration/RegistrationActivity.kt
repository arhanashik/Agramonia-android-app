package com.workfort.apps.agramonia.ui.farmer.registration

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import com.workfort.apps.agramonia.R
import com.workfort.apps.agramonia.data.local.constant.Const
import com.workfort.apps.agramonia.data.local.prefs.PrefsGlobal
import com.workfort.apps.agramonia.data.local.prefs.PrefsUser
import com.workfort.apps.agramonia.databinding.ActivityRegistrationBinding
import com.workfort.apps.agramonia.ui.base.callback.ItemClickListener
import com.workfort.apps.agramonia.ui.base.helper.LinearHorizontalMarginItemDecoration
import com.workfort.apps.agramonia.ui.farmer.createblog.PhotoAdapter
import com.workfort.apps.agramonia.ui.farmer.profile.ProfileActivity
import com.workfort.apps.util.helper.*
import com.workfort.apps.util.lib.remote.ApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import timber.log.Timber
import java.io.File

class RegistrationActivity: AppCompatActivity() {

    private lateinit var mBinding: ActivityRegistrationBinding

    private var disposable: Disposable? = null

    private val apiService by lazy {
        ApiClient.create()
    }

    private var userImgUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ViewUtils.initializeRecyclerView(mBinding.rvPhotos, PhotoAdapter(),
                object : ItemClickListener<Uri> {
                    override fun onItemClick(view: View, item: Uri, position: Int) {
                        when (view.id) {
                            R.id.image_view_cross -> {
                                getPhotoAdapter().removeItem(item)
                                enableAddPhotoButton(getPhotoAdapter().itemCount)
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
        mBinding.tvAddPhoto.text = txt
        val enableIt = adapterSize < 5
        mBinding.tvAddPhoto.isEnabled = enableIt
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            val pickedImageInfo = ImagePicker.getPickedImageInfo(this, resultCode, data)

            if(pickedImageInfo != null && pickedImageInfo.imageUri != null) {
                when (requestCode) {
                    Const.RequestCode.PIC_USER_PHOTO -> {
                        userImgUri = pickedImageInfo.imageUri

                        mBinding.btnChoosePhoto.visibility = View.INVISIBLE
                        mBinding.imgProfile.visibility = View.VISIBLE

                        ImageLoader.load(this, mBinding.imgProfile, userImgUri.toString())
                    }
                    ImagePicker.REQUEST_CODE_PICK_IMAGE -> {
                        getPhotoAdapter().addItem(pickedImageInfo.imageUri)
                        enableAddPhotoButton(getPhotoAdapter().itemCount)
                    }
                    else -> {

                    }
                }
            }

        } else {
            if (resultCode != Activity.RESULT_CANCELED) {
                showToast("Could not pick image")
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
                        onClickPickImage(mBinding.tvAddPhoto)
                    } else {
                        Toast.makeText(this, "Permission is denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun onClickPickUserImage(view: View) {
        if (PermissionUtil.on().request(this,
                        PermissionUtil.REQUEST_CODE_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            ImagePicker.pickImage(this, Const.RequestCode.PIC_USER_PHOTO)
        }
    }

    fun onClickPickImage(view: View) {
        if (PermissionUtil.on().request(this,
                        PermissionUtil.REQUEST_CODE_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            ImagePicker.pickImage(this)
        }
    }

    fun onClickRegistration(view: View){
        val name: String
        val location: String
        val phone: String
        val email: String
        val password: String
        val ans1: String
        val ans2: String
        val ans3: String
        val ans4: String
        val ans5: String

        if(mBinding.etName.text == null){
            setError(true, getString(R.string.name_required_exception), mBinding.tilName)
            return
        }else {
            name = mBinding.etName.text.toString()

            if(TextUtils.isEmpty(name)){
                setError(true, getString(R.string.name_required_exception), mBinding.tilName)
                return
            }else {
                setError(false, null, mBinding.tilName)
            }
        }

        if(userImgUri == null) {
            showToast(getString(R.string.image_required_exception))
            return
        }

        if(mBinding.etLocation.text == null){
            setError(true, getString(R.string.location_required_exception), mBinding.tilLocation)
            return
        }else {
            location = mBinding.etLocation.text.toString()

            if(TextUtils.isEmpty(location)) {
                setError(true, getString(R.string.location_required_exception), mBinding.tilLocation)
                return
            }else {
                setError(false, null, mBinding.tilLocation)
            }
        }

        if(mBinding.etPhone.text == null){
            setError(true, getString(R.string.phone_required_exception), mBinding.tilPhone)
            return
        }else {
            phone = mBinding.etPhone.text.toString()

            if(TextUtils.isEmpty(phone)) {
                setError(true, getString(R.string.phone_required_exception), mBinding.tilPhone)
                return
            }else {
                setError(false, null, mBinding.tilPhone)
            }
        }

        if(mBinding.etEmail.text == null){
            setError(true, getString(R.string.email_required_exception), mBinding.tilEmail)
            return
        }else {
            email = mBinding.etEmail.text.toString()

            if(TextUtils.isEmpty(email)) {
                setError(true, getString(R.string.email_required_exception), mBinding.tilEmail)
                return
            }else {
                setError(false, null, mBinding.tilEmail)
            }
        }

        if(mBinding.etPassword.text == null){
            setError(true, getString(R.string.password_required_exception), mBinding.tilPassword)
            return
        }else {
            password = mBinding.etPassword.text.toString()

            if(TextUtils.isEmpty(password)) {
                setError(true, getString(R.string.password_required_exception), mBinding.tilPassword)
                return
            }else {
                setError(false, null, mBinding.tilPassword)
            }
        }

        if(mBinding.etAnswer1.text == null){
            setError(true, getString(R.string.answer_required_exception), mBinding.tilAnswer1)
            return
        }else {
            ans1 = mBinding.etAnswer1.text.toString()

            if(TextUtils.isEmpty(ans1)) {
                setError(true, getString(R.string.answer_required_exception), mBinding.tilAnswer1)
                return
            }else {
                setError(false, null, mBinding.tilAnswer1)
            }
        }

        if(mBinding.etAnswer2.text == null){
            setError(true, getString(R.string.answer_required_exception), mBinding.tilAnswer2)
            return
        }else {
            ans2 = mBinding.etAnswer2.text.toString()

            if(TextUtils.isEmpty(ans2)) {
                setError(true, getString(R.string.answer_required_exception), mBinding.tilAnswer2)
                return
            }else {
                setError(false, null, mBinding.tilAnswer2)
            }
        }

        if(mBinding.etAnswer3.text == null){
            setError(true, getString(R.string.answer_required_exception), mBinding.tilAnswer3)
            return
        }else {
            ans3 = mBinding.etAnswer3.text.toString()

            if(TextUtils.isEmpty(ans3)) {
                setError(true, getString(R.string.answer_required_exception), mBinding.tilAnswer3)
                return
            }else {
                setError(false, null, mBinding.tilAnswer3)
            }
        }

        if(mBinding.etAnswer4.text == null){
            setError(true, getString(R.string.answer_required_exception), mBinding.tilAnswer4)
            return
        }else {
            ans4 = mBinding.etAnswer4.text.toString()

            if(TextUtils.isEmpty(ans4)) {
                setError(true, getString(R.string.answer_required_exception), mBinding.tilAnswer4)
                return
            }else {
                setError(false, null, mBinding.tilAnswer4)
            }
        }

        if(mBinding.etAnswer5.text == null){
            setError(true, getString(R.string.answer_required_exception), mBinding.tilAnswer5)
            return
        }else {
            ans5 = mBinding.etAnswer5.text.toString()

            if(TextUtils.isEmpty(ans5)) {
                setError(true, getString(R.string.answer_required_exception), mBinding.tilAnswer5)
                return
            }else {
                setError(false, null, mBinding.tilAnswer5)
            }
        }

        if(getPhotoAdapter().itemCount == 0) {
            showToast("Please choose your family image(s)")
            return
        }

        val dialog = AlertDialog.Builder(this)
                .setMessage(getString(R.string.create_profile_confirmation))
                .setPositiveButton(getString(R.string.label_create))
                {
                    dialog, _ ->
                    dialog.dismiss()
                    val ans = arrayOf(ans1, ans2, ans3, ans4, ans5)
                    createProfile(name, location, phone, email, password, ans)
                }
                .setNegativeButton(getString(R.string.label_cancel))
                {
                    dialog, _->
                    dialog.dismiss()
                }
                .create()

        dialog.show()


    }

    private fun createProfile(name: String, location: String, phone: String,
                              email: String, password: String, ans: Array<String>) {
        val userImgPath = ImageUtil.getPath(this, userImgUri)
        val userImageMediaTypeStr = contentResolver.getType(userImgUri!!)
        var proPic: String

        if(userImageMediaTypeStr != null) {
            blockUi(true)

            val userImageMediaType = MediaType.parse(userImageMediaTypeStr)
            val userImageRequestBody = RequestBody.create(userImageMediaType, File(userImgPath))
            val userImageMultipartBody = MultipartBody.Part.createFormData("file",
                    userImgPath, userImageRequestBody)

            showToast("Uploading profile image...")
            disposable = apiService.uploadImage(userImageMultipartBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        response ->
                        showToast(response.message)

                        proPic = response.url
                        showToast("Uploading ${ getPhotoAdapter().itemCount} family image(s)...")
                        val uriList = getPhotoAdapter().getItems()
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

                            disposable = apiService.uploadMultipleImage(multipartBodyParts)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        response1 ->
                                        showToast(response1.message + ". Completing registration...")

                                        doRegistration(name, proPic, location, phone, email,
                                                password, ans, response1.urls)
                                    }, {
                                        error ->
                                        blockUi(false)
                                        Timber.e(error)
                                        showToast(error.message.toString())
                                    })
                        }else {
                            doRegistration(name, proPic, location, phone, email,
                                    password, ans, ArrayList())
                        }
                    }, {
                        error ->
                        Timber.e(error)
                        showToast(error.message.toString())
                        blockUi(false)
                    })
        }
    }

    private fun doRegistration(name: String, image: String, location: String, phone: String,
                               email: String, password: String, ans: Array<String>, images: List<String>) {

        var ansRo = arrayOf("", "", "", "", "")
        var ansEn = arrayOf("", "", "", "", "")
        var ansDe = arrayOf("", "", "", "", "")

        when(PrefsGlobal.selectedLanguageCode) {
            Const.LanguageCode.ROMANIAN -> ansRo = ans
            Const.LanguageCode.GERMANY -> ansDe = ans
            else -> ansEn = ans
        }

        val answerRoJson = JSONObject()
        answerRoJson.put(getString(R.string.question_1_ro), ansRo[0])
        answerRoJson.put(getString(R.string.question_2_ro), ansRo[1])
        answerRoJson.put(getString(R.string.question_3_ro), ansRo[2])
        answerRoJson.put(getString(R.string.question_4_ro), ansRo[3])
        answerRoJson.put(getString(R.string.question_5_ro), ansRo[4])

        val answerEnJson = JSONObject()
        answerEnJson.put(getString(R.string.question_1_en), ansEn[0])
        answerEnJson.put(getString(R.string.question_2_en), ansEn[1])
        answerEnJson.put(getString(R.string.question_3_en), ansEn[2])
        answerEnJson.put(getString(R.string.question_4_en), ansEn[3])
        answerEnJson.put(getString(R.string.question_5_en), ansEn[4])

        val answerDeJson = JSONObject()
        answerDeJson.put(getString(R.string.question_1_de), ansDe[0])
        answerDeJson.put(getString(R.string.question_2_de), ansDe[1])
        answerDeJson.put(getString(R.string.question_3_de), ansDe[2])
        answerDeJson.put(getString(R.string.question_4_de), ansDe[3])
        answerDeJson.put(getString(R.string.question_5_de), ansDe[4])

        disposable = apiService.registration(name, image, location, phone, email, password,
                answerRoJson.toString(), answerEnJson.toString(), answerDeJson.toString(), images)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    response ->
                    showToast(response.message)
                    blockUi(false)
                    PrefsUser.farmer = response.farmer
                    PrefsUser.session = true

                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                }, {
                    error ->
                    Timber.e(error)
                    showToast(error.message.toString())
                    blockUi(false)
                })
    }

    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setError(error: Boolean, message: String?, view: TextInputLayout) {
        view.error = message
        view.isErrorEnabled = error
    }

    fun getPhotoAdapter(): PhotoAdapter {
        return mBinding.rvPhotos.adapter as PhotoAdapter
    }

    private fun blockUi(block: Boolean){
        mBinding.tilName.isEnabled = !block
        mBinding.btnChoosePhoto.isEnabled = !block
        mBinding.tilLocation.isEnabled = !block
        mBinding.tilPhone.isEnabled = !block
        mBinding.tilEmail.isEnabled = !block
        mBinding.tilPassword.isEnabled = !block
        mBinding.tilAnswer1.isEnabled = !block
        mBinding.tilAnswer2.isEnabled = !block
        mBinding.tilAnswer3.isEnabled = !block
        mBinding.tilAnswer4.isEnabled = !block
        mBinding.tilAnswer5.isEnabled = !block
        mBinding.btnRegister.isEnabled = !block
        mBinding.pb.visibility = if(block) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}

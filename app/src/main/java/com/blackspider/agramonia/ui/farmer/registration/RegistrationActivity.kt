package com.blackspider.agramonia.ui.farmer.registration

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.blackspider.agramonia.R
import com.blackspider.agramonia.databinding.ActivityRegistrationBinding
import com.blackspider.agramonia.ui.farmer.profile.ProfileActivity
import com.blackspider.util.helper.ImageLoader
import com.blackspider.util.helper.ImagePicker
import com.blackspider.util.helper.ImageUtil
import com.blackspider.util.helper.PermissionUtil
import com.blackspider.util.lib.remote.ApiService
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File

class RegistrationActivity: AppCompatActivity() {

    private lateinit var mBinding: ActivityRegistrationBinding

    private var disposable: Disposable? = null

    private val apiService by lazy {
        ApiService.create()
    }

    private var imgUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            ImagePicker.REQUEST_CODE_PICK_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val pickedImageInfo = ImagePicker.getPickedImageInfo(this, resultCode, data)

                    if(pickedImageInfo.imageUri != null) {
                        imgUri = pickedImageInfo.imageUri

                        mBinding.btnChoosePhoto.visibility = View.INVISIBLE
                        mBinding.imgProfile.visibility = View.VISIBLE

                        ImageLoader.load(this, mBinding.imgProfile, imgUri.toString())
                    }
                } else {
                    if (resultCode != Activity.RESULT_CANCELED) {
                        showToast("Could not pick image")
                    }
                }
            }
            else -> {

            }
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
        var proPic: String
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

        if(imgUri == null) {
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

        val path = ImageUtil.getPath(this, imgUri)
        val mediaTypeStr = contentResolver.getType(imgUri!!)

        if(mediaTypeStr != null) {
            blockUi(true)

            val mediaType = MediaType.parse(mediaTypeStr)
            val requestBody = RequestBody.create(mediaType, File(path))
            val multipartBody = MultipartBody.Part.createFormData("file", path, requestBody)

            disposable = apiService.uploadImage(multipartBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        response ->
                        showToast(response.message)

                        proPic = response.url
                        doRegistration(name, proPic, location, phone, email,
                                password, ans1, ans2, ans3, ans4, ans5)
                    }, {
                        error ->
                        Timber.e(error)
                        showToast(error.message.toString())
                        blockUi(false)
                    })
        }
    }

    private fun doRegistration(name: String, image: String, location: String, phone: String,
                               email: String, password: String, ans1: String, ans2: String,
                               ans3: String, ans4: String, ans5: String) {

        val answers: HashMap<String, String> = LinkedHashMap()
        answers[getString(R.string.hint_answer1)] = ans1
        answers[getString(R.string.hint_answer2)] = ans2
        answers[getString(R.string.hint_answer3)] = ans3
        answers[getString(R.string.hint_answer4)] = ans4
        answers[getString(R.string.hint_answer5)] = ans5

        val images: List<String> = ArrayList()

        disposable = apiService.registration(name, image, location, phone,
                email, password, answers, images)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    response ->
                    showToast(response.message)
                    blockUi(false)
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("FARMER", response.farmer)
                    startActivity(intent)
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

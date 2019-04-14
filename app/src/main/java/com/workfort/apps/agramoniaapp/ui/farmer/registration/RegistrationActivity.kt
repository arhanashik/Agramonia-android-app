package com.workfort.apps.agramoniaapp.ui.farmer.registration

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.workfort.apps.agramoniaapp.R
import com.workfort.apps.agramoniaapp.data.local.question.Question
import com.workfort.apps.agramoniaapp.data.local.constant.Const
import com.workfort.apps.agramoniaapp.data.local.prefs.PrefsUser
import com.workfort.apps.agramoniaapp.databinding.ActivityRegistrationBinding
import com.workfort.apps.agramoniaapp.databinding.PromptQaBinding
import com.workfort.apps.agramoniaapp.ui.base.callback.ItemClickListener
import com.workfort.apps.agramoniaapp.ui.base.helper.LinearHorizontalMarginItemDecoration
import com.workfort.apps.agramoniaapp.ui.farmer.createblog.PhotoAdapter
import com.workfort.apps.agramoniaapp.ui.farmer.profile.ProfileActivity
import com.workfort.apps.util.helper.*
import com.workfort.apps.util.lib.remote.ApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import timber.log.Timber

class RegistrationActivity: AppCompatActivity() {

    private lateinit var mBinding: ActivityRegistrationBinding

    private var disposable: Disposable? = null

    private val apiService by lazy {
        ApiClient.create()
    }

    private var name: String? = null
    private var location: String? = null
    private var phone: String? = null
    private var userFamilyImages: ArrayList<String> = ArrayList()

    private val questions = ArrayList<Question>()
    private var totalQuestion = 0
    private var answeredQuestionCount = 0

    private var photoAdapter = PhotoAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ImageLoader.load(mBinding.imgAppLogo, R.drawable.img_logo_only_name)

        questions.addAll(Question.prepareQuestions(this))
        totalQuestion = questions.size

        initRecyclerView(mBinding.rvPhotos)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            val pickedImageInfo = ImagePicker.getPickedImageInfo(this, resultCode, data)

            if(pickedImageInfo != null && pickedImageInfo.imageUri != null) {
                when (requestCode) {
                    Const.RequestCode.PIC_FAMILY_PHOTO -> {
                        (mBinding.rvPhotos.adapter as PhotoAdapter).addItem(
                                pickedImageInfo.imageUri)
                    }
                    Const.RequestCode.PIC_ANSWER_PHOTO -> {
                        photoAdapter.addItem(pickedImageInfo.imageUri)
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

                when (permissions[i]) {
                    Manifest.permission.CAMERA -> {
                    }

                    Manifest.permission.READ_EXTERNAL_STORAGE -> {
                    }

                    Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            showToast("Permission is denied")
                        }
                    }
                }
            }
        }
    }

    fun onClickPickFamilyImage(view: View) {
        if (PermissionUtil.on().request(this,
                        PermissionUtil.REQUEST_CODE_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            ImagePicker.pickImage(this, Const.RequestCode.PIC_FAMILY_PHOTO)
        }
    }

    private fun onClickPickAnswerImage() {
        if (PermissionUtil.on().request(this,
                        PermissionUtil.REQUEST_CODE_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            ImagePicker.pickImage(this, Const.RequestCode.PIC_ANSWER_PHOTO)
        }
    }

    fun onClickRegistration(view: View){
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

        if((mBinding.rvPhotos.adapter as PhotoAdapter).itemCount == 0) {
            showToast("Choose at least 1 family photo(s)")
            return
        }

        if(answeredQuestionCount < totalQuestion) {
            showNextAnswerPrompt()
        } else createProfileConfirmation()
    }

    private fun showNextAnswerPrompt() {
        val binding = DataBindingUtil.inflate<PromptQaBinding>(
                layoutInflater, R.layout.prompt_qa, null, false)

        val question = questions[answeredQuestionCount]
        binding.tvQ.text = question.question
        if(question.hasImage) {
            binding.rvPhotos.visibility = View.VISIBLE
            binding.btnAddPhoto.visibility = View.VISIBLE

            initRecyclerView(binding.rvPhotos)

            binding.btnAddPhoto.setOnClickListener {
                if(photoAdapter.itemCount == 3) {
                    Toaster(this).showToast(R.string.max_photo_added)
                } else onClickPickAnswerImage()
            }
        }

        val ansDialog = AlertDialog.Builder(this)
                .setView(binding.root)
                .create()

        binding.btnSave.setOnClickListener {
            if(binding.etAns.text == null) {
                setError(true, getString(R.string.answer_required_exception),
                        binding.tilAns)
            } else {
                val answer = binding.etAns.text.toString()

                if(TextUtils.isEmpty(answer)) {
                    setError(true, getString(R.string.answer_required_exception),
                            binding.tilAns)
                }else {
                    setError(false, null, binding.tilAns)
                    var shouldSave = true

                    if(questions[answeredQuestionCount].hasImage) {
                        if(photoAdapter.itemCount == 0) {
                            shouldSave = false
                            Toaster(this).showToast(R.string.image_required_exception)
                        }
                    }

                    if(shouldSave) saveAnsConfirmation(answer, binding.pb, ansDialog)
                }
            }
        }

        ansDialog.show()
    }

    private fun saveAnsConfirmation(answer: String, pb: ProgressBar,
                                    ansDialog: AlertDialog) {
        val dialog = AlertDialog.Builder(this)
                .setMessage(getString(R.string.question_save_message))
                .setPositiveButton(getString(R.string.label_save))
                {
                    dialog, _ ->
                    dialog.dismiss()
                    questions[answeredQuestionCount].answer = answer

                    if(questions[answeredQuestionCount].hasImage) {
                        pb.visibility = View.VISIBLE
                        ansDialog.setCancelable(false)
                        saveAnsImages(ansDialog)
                    } else {
                        ansDialog.dismiss()
                        answeredQuestionCount++
                        if(answeredQuestionCount < totalQuestion) showNextAnswerPrompt()
                        else createProfileConfirmation()
                    }
                }
                .setNegativeButton(getString(R.string.label_cancel))
                {
                    dialog, _->
                    dialog.dismiss()
                }
                .create()

        dialog.show()
    }

    private fun saveAnsImages(ansDialog: AlertDialog) {
        val multipartBodyParts = ImageUtil.getMultiPartBody(
                photoAdapter.getItems())

        val prefix = RequestBody.create(
                MediaType.parse("text/plain"), Const.Prefix.QA
        )
        disposable = apiService.uploadMultipleImage(prefix, multipartBodyParts)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    response ->
                    questions[answeredQuestionCount].images.clear()
                    questions[answeredQuestionCount].images.addAll(response.urls)
                    ansDialog.dismiss()
                    answeredQuestionCount++
                    if(answeredQuestionCount < totalQuestion) showNextAnswerPrompt()
                    else createProfileConfirmation()
                }, {
                    error ->
                    blockUi(false)
                    Timber.e(error)
                    showToast(getString(R.string.unknown_exception))
                })
    }

    private fun createProfileConfirmation() {
        val dialog = AlertDialog.Builder(this)
                .setMessage(getString(R.string.create_profile_confirmation))
                .setPositiveButton(getString(R.string.label_create))
                {
                    dialog, _ ->
                    dialog.dismiss()

                    createProfile()
                }
                .setNegativeButton(getString(R.string.label_cancel))
                {
                    dialog, _->
                    dialog.dismiss()
                }
                .create()

        dialog.show()
    }

    private fun createProfile() {
        val adapter = mBinding.rvPhotos.adapter as PhotoAdapter

        if(adapter.getItems().isEmpty()) {
            showToast("Please select at least one family image")
            return
        }

        showToast("Uploading ${ adapter.itemCount} family image(s)...")
        val multipartBodyParts = ImageUtil
                .getMultiPartBody(adapter.getItems())

        val prefix = RequestBody.create(
                MediaType.parse("text/plain"), Const.Prefix.FAMILY
        )
        disposable = apiService.uploadMultipleImage(prefix, multipartBodyParts)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    response ->
                    showToast(response.message + ". Completing registration...")

                    userFamilyImages.clear()
                    userFamilyImages.addAll(response.urls)
                    doRegistration()
                }, {
                    error ->
                    blockUi(false)
                    Timber.e(error)
                    showToast(error.message.toString())
                    userFamilyImages.clear()
                })
    }

    private fun doRegistration() {

        val ansRo = Question.prepareAnswersJsonStr(questions,
                Const.LanguageCode.ROMANIAN)
        val ansDe = Question.prepareAnswersJsonStr(questions,
                Const.LanguageCode.GERMANY)
        val ansEn = Question.prepareAnswersJsonStr(questions,
                Const.LanguageCode.ENGLISH)
        val ansImages = Question.prepareAnsImageJsonStr(questions)

        disposable = apiService.registration(name!!, location!!, phone!!,
                userFamilyImages[0], ansRo, ansDe, ansEn, ansImages, userFamilyImages)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    response ->
                    showToast(response.message)
                    blockUi(false)
                    if(response.success) {
                        PrefsUser.family = response.family
                        PrefsUser.session = true

                        startActivity(Intent(this, ProfileActivity::class.java))
                        finish()
                    }
                }, {
                    error ->
                    Timber.e(error)
                    showToast(error.message.toString())
                    blockUi(false)
                })
    }

    private fun initRecyclerView(rv: RecyclerView) {
        photoAdapter = PhotoAdapter()
        ViewUtils.initializeRecyclerView(rv, photoAdapter,
                object : ItemClickListener<Uri> {
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

    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setError(error: Boolean, message: String?, view: TextInputLayout) {
        view.error = message
        view.isErrorEnabled = error
    }

    private fun blockUi(block: Boolean){
        mBinding.tilName.isEnabled = !block
        mBinding.tilLocation.isEnabled = !block
        mBinding.tilPhone.isEnabled = !block
        mBinding.btnRegister.isEnabled = !block
        mBinding.btnAddPhoto.isEnabled = !block
        mBinding.pb.visibility = if(block) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}

package com.workfort.apps.agramoniaapp.ui.farmer.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.workfort.apps.agramoniaapp.R
import com.workfort.apps.agramoniaapp.data.local.prefs.PrefsUser
import com.workfort.apps.agramoniaapp.databinding.ActivityLoginBinding
import com.workfort.apps.agramoniaapp.ui.farmer.profile.ProfileActivity
import com.workfort.apps.agramoniaapp.ui.farmer.registration.RegistrationActivity
import com.workfort.apps.util.helper.ImageLoader
import com.workfort.apps.util.lib.remote.ApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class LoginActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityLoginBinding

    private var disposable: Disposable? = null

    private val apiService by lazy {
        ApiClient.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ImageLoader.load(this, mBinding.imgAppLogo, R.drawable.img_logo_only_name)
    }

    fun onClickLogin(view: View){

        val phone: String

        if(mBinding.etPhone.text == null){
            mBinding.tilPhone.isErrorEnabled = true
            mBinding.tilPhone.error = getString(R.string.phone_required_exception)
            return
        }else {
            mBinding.tilPhone.error = null
            mBinding.tilPhone.isErrorEnabled = false
            phone = mBinding.etPhone.text.toString()
        }

        if(TextUtils.isEmpty(phone)){
            mBinding.tilPhone.error = getString(R.string.phone_required_exception)
            return
        }

        checkLogIn(phone)
    }

    fun onClickRegister(view: View){
        startActivity(Intent(this, RegistrationActivity::class.java))
    }

    private fun checkLogIn(phone: String){

        blockUi(true)

        disposable = apiService.login(phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    response ->
                    showToast(response.message)
                    blockUi(false)
                    if(response.success) {
                        PrefsUser.farmer = response.farmer
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

    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun blockUi(block: Boolean){
        mBinding.tilPhone.isEnabled = !block
        mBinding.btnLogin.isEnabled = !block
        mBinding.btnRegister.isEnabled = !block
        mBinding.pb.visibility = if(block) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}

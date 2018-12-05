package com.blackspider.agramonia.ui.farmer.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.blackspider.agramonia.R
import com.blackspider.agramonia.databinding.ActivityLoginBinding
import com.blackspider.agramonia.ui.farmer.profile.ProfileActivity
import com.blackspider.agramonia.ui.farmer.registration.RegistrationActivity
import com.blackspider.util.lib.remote.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class LoginActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityLoginBinding

    private var disposable: Disposable? = null

    private val apiService by lazy {
        ApiService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun onClickLogin(view: View){

        val email: String
        val password: String

        if(mBinding.etEmail.text == null){
            mBinding.tilEmail.isErrorEnabled = true
            mBinding.tilEmail.error = getString(R.string.email_required_exception)
            return;
        }else {
            mBinding.tilEmail.error = null
            mBinding.tilEmail.isErrorEnabled = false
            email = mBinding.etEmail.text.toString()
        }

        if(mBinding.etPassword.text == null){
            mBinding.tilPassword.isErrorEnabled = true
            mBinding.tilPassword.error = getString(R.string.password_required_exception)
            return
        }else {
            mBinding.tilPassword.error = null
            mBinding.tilPassword.isErrorEnabled = false
            password = mBinding.etPassword.text.toString()
        }

        if(TextUtils.isEmpty(email)){
            mBinding.tilEmail.error = getString(R.string.email_required_exception)
            return
        }

        if(TextUtils.isEmpty(password)){
            mBinding.tilPassword.error = getString(R.string.password_required_exception)
            return
        }

        checkLogIn(email, password);
    }

    fun onClickRegister(view: View){
        startActivity(Intent(this, RegistrationActivity::class.java))
    }

    private fun checkLogIn(email: String, password: String){

        blockUi(true)

        disposable = apiService.login(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    response ->
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

    private fun blockUi(block: Boolean){
        mBinding.tilEmail.isEnabled = !block
        mBinding.tilPassword.isEnabled = !block
        mBinding.btnLogin.isEnabled = !block
        mBinding.btnRegister.isEnabled = !block
        mBinding.pb.visibility = if(block) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}

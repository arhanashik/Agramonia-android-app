package com.blackspider.agramonia.ui.farmer.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.blackspider.agramonia.R;
import com.blackspider.agramonia.databinding.ActivityLoginBinding;
import com.blackspider.agramonia.ui.farmer.registration.RegistrationActivity;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    public void onClickLogin(View view){
        String email, password;
        if(mBinding.etEmail.getText() == null){
            mBinding.tilEmail.setErrorEnabled(true);
            mBinding.tilEmail.setError(getString(R.string.email_required_exception));
            return;
        }else {
            mBinding.tilEmail.setError(null);
            mBinding.tilEmail.setErrorEnabled(false);
            email = mBinding.etEmail.getText().toString();
        }

        if(mBinding.etPassword.getText() == null){
            mBinding.tilPassword.setErrorEnabled(true);
            mBinding.tilPassword.setError(getString(R.string.password_required_exception));
            return;
        }else {
            mBinding.tilPassword.setError(null);
            mBinding.tilPassword.setErrorEnabled(false);
            password = mBinding.etPassword.getText().toString();
        }

        if(TextUtils.isEmpty(email)){
            mBinding.tilEmail.setError(getString(R.string.email_required_exception));
            return;
        }

        if(TextUtils.isEmpty(password)){
            mBinding.tilPassword.setError(getString(R.string.password_required_exception));
            return;
        }

        checkLogIn(email, password);
    }

    public void onClickRegister(View view){
        startActivity(new Intent(this, RegistrationActivity.class));
    }

    private void checkLogIn(String email, String password){
        mBinding.tilEmail.setEnabled(false);
        mBinding.tilPassword.setEnabled(false);
        mBinding.btnLogin.setEnabled(false);
        mBinding.pb.setVisibility(View.VISIBLE);
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

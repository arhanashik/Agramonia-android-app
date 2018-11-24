package com.blackspider.agramonia.ui.farmer.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.blackspider.agramonia.R;
import com.blackspider.agramonia.databinding.ActivityLoginBinding;

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
        if(mBinding.etEmail.getText() != null){
            email = mBinding.etEmail.getText().toString();
        }else {
            mBinding.tilEmail.setError(getString(R.string.email_required_exception));
            return;
        }
        if(mBinding.etPassword.getText() != null){
            password = mBinding.etPassword.getText().toString();
        }else {
            mBinding.tilPassword.setError(getString(R.string.password_required_exception));
            return;
        }

        if(TextUtils.isEmpty(email)){
            mBinding.tilEmail.setError(getString(R.string.email_required_exception));
            return;
        }

        if(TextUtils.isEmpty(password)){
            mBinding.tilPassword.setError(getString(R.string.email_required_exception));
            return;
        }

        checkLogIn(email, password);
    }

    private void checkLogIn(String email, String password){

    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

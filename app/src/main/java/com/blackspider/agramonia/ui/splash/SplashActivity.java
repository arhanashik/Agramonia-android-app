package com.blackspider.agramonia.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.blackspider.agramonia.R;
import com.blackspider.agramonia.databinding.ActivitySplashBinding;
import com.blackspider.agramonia.ui.home.HomeActivity;
import com.bumptech.glide.Glide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActivitySplashBinding mBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_splash);

        Glide.with(this)
                .load(R.mipmap.ic_launcher)
                .into(mBinding.imgAppLogo);

        Glide.with(this)
                .load(R.drawable.img_logo_only_name)
                .into(mBinding.imgAppName);

        long animStartTime = AnimationUtils.currentAnimationTimeMillis() + 10;
        Animation leftIn = AnimationUtils.loadAnimation(this, R.anim.left_in);
        leftIn.setStartTime(animStartTime);

        Animation rightIn = AnimationUtils.loadAnimation(this, R.anim.right_in);
        rightIn.setStartTime(animStartTime);

        mBinding.imgAppLogo.setAnimation(leftIn);
        mBinding.imgAppName.setAnimation(rightIn);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }, getResources().getInteger(R.integer.splash_time));
    }
}

package com.workfort.apps.agramonia.ui.splash;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.workfort.apps.agramonia.R;
import com.workfort.apps.agramonia.data.local.constant.Const;
import com.workfort.apps.agramonia.data.local.prefs.PrefsGlobal;
import com.workfort.apps.agramonia.databinding.ActivitySplashBinding;
import com.workfort.apps.agramonia.ui.home.HomeActivity;
import com.workfort.apps.util.helper.ImageLoader;

import java.util.Locale;

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

        ImageLoader.load(this, mBinding.imgAppLogo, R.mipmap.ic_launcher);
        ImageLoader.load(this, mBinding.imgAppName, R.drawable.img_logo_only_name);

        long animStartTime = AnimationUtils.currentAnimationTimeMillis() + 10;
        Animation leftIn = AnimationUtils.loadAnimation(this, R.anim.left_in);
        leftIn.setStartTime(animStartTime);

        Animation rightIn = AnimationUtils.loadAnimation(this, R.anim.right_in);
        rightIn.setStartTime(animStartTime);

        mBinding.imgAppLogo.setAnimation(leftIn);
        mBinding.imgAppName.setAnimation(rightIn);

        if (PrefsGlobal.INSTANCE.getFirstRun()) {
            PrefsGlobal.INSTANCE.setFirstRun(false);
            PrefsGlobal.INSTANCE.setSelectedLanguageCode(Const.LanguageCode.ROMANIAN);
            setLanguage();
            return;
        }

        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }, getResources().getInteger(R.integer.splash_time));
    }

    private void setLanguage() {
        Locale locale = new Locale(PrefsGlobal.INSTANCE.getSelectedLanguageCode());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        finishAffinity();
        startActivity(new Intent(this, SplashActivity.class));
    }
}

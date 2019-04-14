package com.workfort.apps.agramoniaapp.ui.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.workfort.apps.agramoniaapp.R;
import com.workfort.apps.agramoniaapp.data.local.constant.Const;
import com.workfort.apps.agramoniaapp.data.local.prefs.PrefsGlobal;
import com.workfort.apps.agramoniaapp.data.local.prefs.PrefsUser;
import com.workfort.apps.agramoniaapp.databinding.ActivityHomeBinding;
import com.workfort.apps.agramoniaapp.ui.farmer.login.LoginActivity;
import com.workfort.apps.agramoniaapp.ui.farmer.profile.ProfileActivity;
import com.workfort.apps.agramoniaapp.ui.splash.SplashActivity;
import com.workfort.apps.agramoniaapp.ui.tourist.TouristActivity;
import com.workfort.apps.util.helper.ImageLoader;

import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class HomeActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityHomeBinding mBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_home);

        ImageLoader.load(mBinding.imgAppLogo, R.drawable.img_logo_only_name);

        for (int id : mBinding.groupTourist.getReferencedIds()) {
            findViewById(id).setOnClickListener(view ->
                    startActivity(new Intent(this, TouristActivity.class)));
        }

        for (int id : mBinding.groupFarmer.getReferencedIds()) {
            findViewById(id).setOnClickListener(view -> {
                intent = new Intent(this, LoginActivity.class);
                if(PrefsUser.INSTANCE.getSession()) {
                    intent = new Intent(this, ProfileActivity.class);
                }

                startActivity(intent);
            });
        }
    }

    public void onClickChangeLanguage(View view) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.select_language))
                .setPositiveButton(Const.Language.ENGLISH, (dialogInterface, i) -> {
                    changeLanguage(Const.LanguageCode.ENGLISH);
                    dialogInterface.dismiss();
                })
                .setNegativeButton(Const.Language.ROMANIAN, (dialogInterface, i) -> {
                    changeLanguage(Const.LanguageCode.ROMANIAN);
                    dialogInterface.dismiss();
                })
                .setNeutralButton(Const.Language.GERMANY, (dialogInterface, i) -> {
                    changeLanguage(Const.LanguageCode.GERMANY);
                    dialogInterface.dismiss();
                })
                .create();

        dialog.show();
    }

    private void changeLanguage(String languageCode) {
        PrefsGlobal.INSTANCE.setSelectedLanguageCode(languageCode);

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        finishAffinity();
        startActivity(new Intent(this, SplashActivity.class));
    }
}

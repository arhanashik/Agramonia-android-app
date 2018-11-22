package com.blackspider.agramonia.ui.home;

import android.content.Intent;
import android.os.Bundle;

import com.blackspider.agramonia.R;
import com.blackspider.agramonia.databinding.ActivityHomeBinding;
import com.blackspider.agramonia.ui.tourist.TouristActivity;
import com.blackspider.util.helper.ImageLoader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityHomeBinding mBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_home);

        ImageLoader.load(this, mBinding.imgAppLogo, R.drawable.img_logo_only_name);

        for (int id : mBinding.groupTourist.getReferencedIds()) {
            findViewById(id).setOnClickListener(view ->
                    startActivity(new Intent(this, TouristActivity.class)));
        }

        for (int id : mBinding.groupFarmer.getReferencedIds()) {
            findViewById(id).setOnClickListener(view -> {
                //startActivity(new Intent(this, TouristActivity.class));
            });
        }
    }
}

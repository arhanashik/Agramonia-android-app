package com.blackspider.agramonia.ui.createblog;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.blackspider.agramonia.R;
import com.blackspider.agramonia.databinding.ActivityCreateBlogBinding;
import com.blackspider.util.helper.ImageInfo;
import com.blackspider.util.helper.ImagePicker;
import com.blackspider.util.helper.PermissionUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class CreateBlogActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityCreateBlogBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_blog);

        setSupportActionBar(mBinding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.create_blog));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mBinding.textViewCreate.setOnClickListener(this);
        mBinding.textViewAddPhoto.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_view_create:
                onBackPressed();
                break;

            case R.id.text_view_add_photo:
                pickImage();
                break;

            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionUtil.REQUEST_CODE_STORAGE) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];

                switch (permission) {
                    case Manifest.permission.CAMERA:
                        break;

                    case Manifest.permission.READ_EXTERNAL_STORAGE:
                        break;

                    case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            pickImage();
                        } else {
                            Toast.makeText(this, "Permission is denied", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        }
    }

    private void pickImage() {
        if (PermissionUtil.on().request(this,
                PermissionUtil.REQUEST_CODE_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            ImagePicker.pickImage(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImagePicker.REQUEST_CODE_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                ImageInfo pickedImageInfo =
                        ImagePicker.getPickedImageInfo(this, resultCode, data);


            } else {
                if (resultCode != RESULT_CANCELED) {
                    Toast.makeText(this, "Could not pick image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

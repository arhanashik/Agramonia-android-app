package com.blackspider.util.lib.barcodescanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.blackspider.agramonia.R;
import com.google.zxing.Result;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class SimpleScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler  {
    public static final int REQUEST_CODE_BARCODE_SCANNER = 111;
    public static final String KEY_SCANNED_TEXT = "SCANNED_TEXT";
    public static final String KEY_SCANNED_FORMAT = "SCANNED_FORMAT";

    private final int REQUEST_CODE_CAMERA = 999;
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

        //this parameter is needed for Huawei devices like P9, P10
        mScannerView.setAspectTolerance(0.5f);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        if(hasCameraPermission())
            mScannerView.startCamera(); // Start camera on resume
        else
            requestForCameraPermission();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        setResult(RESULT_CANCELED, resultIntent);

        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
            int[] grantResults) {
        if(requestCode == REQUEST_CODE_CAMERA){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mScannerView.startCamera();
            } else {
                Toast.makeText(this, getString(R.string.camera_permission_denied_exception),
                        Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void handleResult(Result rawResult) {
        Intent resultIntent = new Intent();

        if(rawResult != null){
            resultIntent.putExtra(KEY_SCANNED_TEXT, rawResult.getText());
            resultIntent.putExtra(KEY_SCANNED_FORMAT,
                    rawResult.getBarcodeFormat().toString());
        }

        setResult(RESULT_OK, resultIntent);
        finish();
        //mScannerView.resumeCameraPreview(this);
    }

    public boolean hasCameraPermission(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void requestForCameraPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                REQUEST_CODE_CAMERA);
    }
}

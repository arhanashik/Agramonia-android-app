package com.blackspider.agramonia.ui.tourist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.blackspider.agramonia.R;
import com.blackspider.util.lib.barcodescanner.SimpleScannerActivity;

import java.util.Objects;

public class TouristActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    public void onClickScanner(View view){
        Intent intent = new Intent(this, SimpleScannerActivity.class);
        startActivityForResult(intent, SimpleScannerActivity.REQUEST_CODE_BARCODE_SCANNER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == SimpleScannerActivity.REQUEST_CODE_BARCODE_SCANNER){

            if(resultCode == RESULT_OK && data != null){
                String scannedText = data.getStringExtra(SimpleScannerActivity.KEY_SCANNED_TEXT);
                //String scannedFormat = data.getStringExtra(SimpleScannerActivity.KEY_SCANNED_FORMAT);

                if(!TextUtils.isEmpty(scannedText)
                        && scannedText.contains(getString(R.string.default_web_address))){
                    openBrowser(scannedText);
                }else {
                    showToast(getString(R.string.invalid_qr_code_exception));
                }
            }else {
                showToast(getString(R.string.scan_canceled_exception));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void openBrowser(String url){
        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            url = "https://" + url;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        try {
            startActivity(i);
        }catch (ActivityNotFoundException ex){
            showToast(getString(R.string.no_browser_found_exception));
        }
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

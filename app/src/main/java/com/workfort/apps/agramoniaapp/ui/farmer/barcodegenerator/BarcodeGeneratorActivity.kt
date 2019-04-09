package com.workfort.apps.agramoniaapp.ui.farmer.barcodegenerator

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.workfort.apps.agramoniaapp.R
import com.workfort.apps.agramoniaapp.data.local.prefs.PrefsUser

import com.workfort.apps.agramoniaapp.databinding.ActivityBarcodeGeneratorBinding

class BarcodeGeneratorActivity: AppCompatActivity() {

    private lateinit var mBinding: ActivityBarcodeGeneratorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_barcode_generator)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val text= "http://${getString(R.string.default_web_address)}" +
                "${getString(R.string.path_to_farmer_profile)}${PrefsUser.family?.id}"
        val multiFormatWriter = MultiFormatWriter()

        mBinding.tvBarcode.text = text
        try {
            val bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,
                    400, 400)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            mBinding.pb.visibility = View.INVISIBLE
            mBinding.imgBarcode.visibility = View.VISIBLE
            mBinding.imgBarcode.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
            mBinding.pb.visibility = View.INVISIBLE
        }
    }
}
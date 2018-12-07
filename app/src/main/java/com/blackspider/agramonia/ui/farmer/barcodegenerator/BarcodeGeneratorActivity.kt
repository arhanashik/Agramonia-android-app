package com.blackspider.agramonia.ui.farmer.barcodegenerator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.blackspider.agramonia.R
import com.blackspider.agramonia.databinding.ActivityBarcodeGeneratorBinding
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import android.view.View
import com.blackspider.agramonia.data.local.prefs.Prefs
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.google.zxing.BarcodeFormat





class BarcodeGeneratorActivity: AppCompatActivity() {

    private lateinit var mBinding: ActivityBarcodeGeneratorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_barcode_generator)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val text= "https://${getString(R.string.default_web_address)}/farmers/id=${Prefs.farmer?.id}"
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
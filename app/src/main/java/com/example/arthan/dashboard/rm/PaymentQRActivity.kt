package com.example.arthan.dashboard.rm

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.arthan.R
import com.example.arthan.global.Crashlytics
import com.example.arthan.lead.LeadInfoCaptureActivity
import com.example.arthan.lead.PaymentSuccessActivity
import com.example.arthan.model.PAYMENT_SCREEN
import com.example.arthan.model.QR_PAYMENT_SCREEN
import com.example.arthan.model.UpdateEligibilityAndPaymentReq
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.ProgrssLoader
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.activity_payment_q_r.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception


class PaymentQRActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_q_r)
        btn_next.setOnClickListener {
            /*startActivity(Intent(this, PaymentSuccessActivity::class.java).apply {
                putExtra("screen","CONSENT")
                putExtra("leadId", intent.getStringExtra("leadId"))
                putExtra("loanId",intent.getStringExtra("loanId"))
                putExtra("custId",intent.getStringExtra("custId"))
                putExtra(ArgumentKey.InPrincipleAmount,intent.getStringExtra(ArgumentKey.InPrincipleAmount))
                putExtra("task","RMContinue")
            })*/
            val progressBar = ProgrssLoader(this)
            progressBar.showLoading()
            CoroutineScope(Dispatchers.IO).launch {

                try {

                    val loanId=intent.getStringExtra("loanId")
                    val response =
                        RetrofitFactory.getApiService().updateEligibilityAndPayment(
                            UpdateEligibilityAndPaymentReq(
                                intent.getStringExtra("leadId"),
                                if (loanId.isNullOrBlank()) "C1234" else loanId, QR_PAYMENT_SCREEN
                            )
                        )

                    if (response.isSuccessful && response.body() != null) {

                        if (response.body()?.apiCode == "200") {

                            withContext(Dispatchers.Main) {
                                progressBar.dismmissLoading()

                                if (intent.getStringExtra("task") == "RMreJourney") {
                                    withContext(Dispatchers.Main) {

                                        startActivity(
                                            Intent(
                                                this@PaymentQRActivity,
                                                PaymentSuccessActivity::class.java
                                            ).apply {
                                                putExtra("loanId", loanId)
                                            }
                                        )
                                        finish()
                                    }

                                } else {
                                    if (response.body()?.eligibility.equals(
                                            "N",
                                            ignoreCase = true
                                        )
                                    ) {
                                        Toast.makeText(
                                            this@PaymentQRActivity,
                                            "We are sorry, your Credit score is low",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        startActivity(
                                            Intent(
                                                this@PaymentQRActivity,
                                                RMDashboardActivity::class.java
                                            )
                                        )
                                        finish()
                                    } else {
                                        startActivity(
                                            Intent(
                                                this@PaymentQRActivity,
                                                LeadInfoCaptureActivity::class.java
                                            ).apply {

                                                putExtra("loanId", response.body()!!.loanId)
                                                putExtra("custId", response.body()!!.customerId)
                                                putExtra(
                                                    "annualturnover",
                                                    response.body()!!.annualTurnover
                                                )
                                                putExtra(
                                                    "businessName",
                                                    response.body()!!.businessName
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                progressBar.dismmissLoading()
                                Toast.makeText(
                                    this@PaymentQRActivity,
                                    "Something went wrong.Please try again..",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Crashlytics.log(e.message)

                    withContext(Dispatchers.Main) {
                        progressBar.dismmissLoading()
                        Toast.makeText(
                            this@PaymentQRActivity,
                            "Something went wrong.Please try again..",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }

        }
        val writer = QRCodeWriter()
        try {
//            val bitMatrix = writer.encode("upi://pay?pa=paytm-909685487@paytm&pn=ARTHAN%20FINANCE&mc=6012&tr=OREDRIDART98765&am=1770.00&cu=INR&paytmqr=2810050501017CZCET3W1547", BarcodeFormat.QR_CODE, 512, 512)
            val bitMatrix = writer.encode(intent.getStringExtra("qrCode"), BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
         qr.setImageBitmap(bmp)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }
}

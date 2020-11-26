package com.example.arthan.lead

import android.content.Intent
import android.util.Log
import com.example.arthan.R
import com.example.arthan.dashboard.rm.RMScreeningNavigationActivity
import com.example.arthan.model.VerifyOTPRequest
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_application_fee.*
import kotlinx.android.synthetic.main.activity_otp_validation.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OTPValidationActivity: BaseActivity() {

    override fun screenTitle()= "Consent"

    override fun contentView()= R.layout.activity_otp_validation

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {
        if(intent.hasExtra("mobNo")){
            val mobNo=intent.getStringExtra("mobNo")
            txt_otp_msg.text = "OTP sent to $mobNo"
        }
        btn_proceed?.isEnabled = false
        chk_consent?.isChecked = false

        chk_consent?.setOnCheckedChangeListener { buttonView, isChecked ->
            btn_submit?.isEnabled = isChecked
        }

        val appFee=intent.getStringExtra("appFee")
        val gst=intent.getStringExtra("gst")
        val total=intent.getStringExtra("total")
        txt_application_fee_amt.text= appFee
        txt_gst_amt.text= gst
        txt_total_amt.text= total
        btn_submit.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {

                val response = RetrofitFactory.getApiService()./*verifyOTP*/verifyOTPAppFee(VerifyOTPRequest(intent.getStringExtra("loanId"),
                intent.getStringExtra("custId"),intent.getStringExtra("leadId"),"",intent.getStringExtra("appFee"),view_otp.otp))

                if(response.isSuccessful && response.body() != null){

                    if(response.body()?.verifyStatus.equals("success",true)) {

                        if (intent.getStringExtra("task") == "RMreJourney") {
                            withContext(Dispatchers.Main) {

                                startActivity(
                                    Intent(
                                        this@OTPValidationActivity,
                                        RMScreeningNavigationActivity::class.java
                                    ).apply {
                                        putExtra("loanId", response.body()!!.loanId)
                                    }
                                )
                                finish()
                            }

                        } else {
                           /* startActivity(
                                Intent(
                                    this@OTPValidationActivity,
                                    ApplicationFeeActivity::class.java
                                ).apply {
                                    putExtra("DATA", response.body())
                                    putExtra("loanId", response.body()!!.loanId)
                                    putExtra("custId", response.body()!!.customerId)
                                })*/
                            startActivity(Intent(
                                this@OTPValidationActivity,
                                PaymentSuccessActivity::class.java
                            )
                                .apply {
                                    putExtra("loanId", response.body()!!.loanId)
                                    putExtra("custId", response.body()!!.customerId)
                                   // putExtra("leadId", response.body()!!.leadId)
                                })
                        }

                    }

                    Log.e("RESPONSE","${response.body().toString()}")
                }

            }


        }
    }
}
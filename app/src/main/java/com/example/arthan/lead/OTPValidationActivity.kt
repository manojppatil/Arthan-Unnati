package com.example.arthan.lead

import android.content.Intent
import android.util.Log
import com.example.arthan.R
import com.example.arthan.dashboard.rm.RMScreeningNavigationActivity
import com.example.arthan.model.VerifyOTPRequest
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.views.activities.BaseActivity
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
        btn_submit.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {

                val response = RetrofitFactory.getApiService().verifyOTP(VerifyOTPRequest(intent.getStringExtra("loanId"),
                intent.getStringExtra("custId"),"123456"))

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
                            startActivity(
                                Intent(
                                    this@OTPValidationActivity,
                                    ApplicationFeeActivity::class.java
                                ).apply {
                                    putExtra("DATA", response.body())
                                    putExtra("loanId", response.body()!!.loanId)
                                    putExtra("custId", response.body()!!.customerId)
                                })
                        }
                    }

                    Log.e("RESPONSE","${response.body().toString()}")
                }

            }


        }
    }
}
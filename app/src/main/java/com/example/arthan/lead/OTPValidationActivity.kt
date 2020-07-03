package com.example.arthan.lead

import android.content.Intent
import android.util.Log
import com.example.arthan.R
import com.example.arthan.model.VerifyOTPRequest
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_otp_validation.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OTPValidationActivity: BaseActivity() {

    override fun screenTitle()= "Consent"

    override fun contentView()= R.layout.activity_otp_validation

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {
        btn_submit.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {

                val response = RetrofitFactory.getRMServiceService().verifyOTP(VerifyOTPRequest("R1234",
                "C1234","123456"))

                if(response.isSuccessful && response.body() != null){

                    if(response.body()?.verifyStatus.equals("success",true)){
                        startActivity(Intent(this@OTPValidationActivity,ApplicationFeeActivity::class.java).apply {
                            putExtra("DATA",response.body())
                        })
                    }

                    Log.e("RESPONSE","${response.body().toString()}")
                }

            }


        }
    }
}
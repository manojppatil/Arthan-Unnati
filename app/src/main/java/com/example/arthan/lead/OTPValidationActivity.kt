package com.example.arthan.lead

import android.content.Intent
import android.util.Log
import com.example.arthan.R
import com.example.arthan.dashboard.rm.PaymentQRActivity
import com.example.arthan.dashboard.rm.RMScreeningNavigationActivity
import com.example.arthan.model.VerifyOTPRequest
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_application_fee.*
import kotlinx.android.synthetic.main.activity_otp_validation.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.HashMap

class OTPValidationActivity: BaseActivity() {

    override fun screenTitle()= "Consent"

    override fun contentView()= R.layout.activity_otp_validation

    override fun onToolbarBackPressed() = onBackPressed()

    private lateinit var appFee: String
    private lateinit var gst: String
    private lateinit var total: String
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

        if (intent.getStringExtra("task") == "RMContinue") {
            loadDataFromServer()

        }else if(intent.getStringExtra("task")=="RMreJourney")
        {
            CoroutineScope(Dispatchers.IO).launch {
                var map = HashMap<String, String>()
                map["loanId"] = intent.getStringExtra("loanId")
                map["screen"] = "OTP_CONSENT"
                val res=RetrofitFactory.getApiService().getScreenData(map)
                if(res.body()!=null)
                {


                    withContext(Dispatchers.Main){

                    appFee=res.body()!!.appFee!!
                    gst=res.body()!!.gst!!
                    total=res.body()!!.total!!
                    txt_application_fee_amt.text= appFee
                    txt_gst_amt.text= gst
                    txt_total_amt.text= total
                    }

                }
            }
        }else {
             appFee = intent.getStringExtra("appFee")
             gst = intent.getStringExtra("gst")
             total = intent.getStringExtra("total")
            txt_application_fee_amt.text= appFee
            txt_gst_amt.text= gst
            txt_total_amt.text= total

        }

        btn_submit.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {

                val response = RetrofitFactory.getApiService()./*verifyOTP*/verifyOTPAppFee(VerifyOTPRequest(intent.getStringExtra("loanId"),
                intent.getStringExtra("custId"),intent.getStringExtra("leadId"),"",appFee,view_otp.otp))

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
                                PaymentQRActivity::class.java
                            )
                                .apply {
                                    putExtra("loanId", response.body()!!.loanId)
                                    putExtra("qrCode", response.body()!!.qrCode)
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

    private fun loadDataFromServer() {


        try {
            val progressLoader = ProgrssLoader(this!!)
            progressLoader.showLoading()
            CoroutineScope(Dispatchers.IO).launch {
                /* val response =
                     RetrofitFactory.getApiService().getIncomeData(arguments?.getString("loanId"))*/
                var map = HashMap<String, String>()
                map["loanId"] = intent?.getStringExtra("loanId")!!
                map["screen"] = "OTP_CONSENT"
                val response =
                    RetrofitFactory.getApiService().getScreenData(map)
                withContext(Dispatchers.Main) {
                    if (response!!.isSuccessful) {
                        val res = response.body()
                        progressLoader.dismmissLoading()
                        updateData(
                            res?.gst,
                            res?.total,
                            res?.appFee,
                            res?.leadId,
                            res?.mobNo
                        )
                    }
                }
            }
        } catch (e: java.lang.Exception) {

        }
    }

    private fun updateData(
        gst: String?,
        total: String?,
        appFee: String?,
        leadId: String?,
        mobNo: String?
    ) {
        txt_application_fee_amt.text= appFee
        txt_gst_amt.text= gst
        txt_total_amt.text= total
        txt_otp_msg.text = "OTP sent to $mobNo"
    }
}
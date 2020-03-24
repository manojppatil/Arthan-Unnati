package com.example.arthan.lead

import android.content.Intent
import android.widget.Toast
import com.example.arthan.R
import com.example.arthan.global.AppPreferences
import com.example.arthan.model.ELIGIBILITY_SCREEN
import com.example.arthan.model.PaymentRequest
import com.example.arthan.model.UpdateEligibilityAndPaymentReq
import com.example.arthan.model.VerifyOTPResponse
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_application_fee.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import com.example.arthan.utils.ArgumentKey


class ApplicationFeeActivity : BaseActivity() {

    override fun contentView() = R.layout.activity_application_fee

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        if(intent.hasExtra("DATA")){

            val data= intent.getSerializableExtra("DATA") as VerifyOTPResponse

            txt_application_fee_amt.text= "${data.appFee}"
            txt_gst_amt.text= "${data.gst}"
            txt_total_amt.text= "${data.total}"

        }

        btn_proceed.setOnClickListener {

            val progressBar = ProgrssLoader(this)
            progressBar.showLoading()

            val leadId= AppPreferences.getInstance().getString(ArgumentKey.LeadId)
            val loanId= AppPreferences.getInstance().getString(AppPreferences.Key.LoanId)
            val customerID= AppPreferences.getInstance().getString(AppPreferences.Key.CustomerId)

            CoroutineScope(Dispatchers.IO).launch {

                try {

                    val response =
                        RetrofitFactory.getApiService().proceedToPay(
                            PaymentRequest(
                                leadId,
                                if (loanId.isNullOrBlank()) "C1234" else loanId,
                                if(customerID.isNullOrBlank()) "" else customerID,
                                txt_total_amt.text.toString()
                            )
                        )

                    if (response.isSuccessful && response.body() != null) {

                        if (response.body()?.apiCode == "200") {

                            withContext(Dispatchers.Main) {
                                progressBar.dismmissLoading()
                                startActivity(Intent(this@ApplicationFeeActivity, PaymentSuccessActivity::class.java))
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                progressBar.dismmissLoading()
                                Toast.makeText(
                                    this@ApplicationFeeActivity,
                                    "Something went wrong.Please try again..",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        progressBar.dismmissLoading()
                        Toast.makeText(
                            this@ApplicationFeeActivity,
                            "Something went wrong.Please try again..",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }


        }

        btn_proceed?.isEnabled = false
        chk_consent?.isChecked = false

        chk_consent?.setOnCheckedChangeListener { buttonView, isChecked ->
            btn_proceed?.isEnabled = isChecked
        }
    }

    override fun screenTitle() = "Application Fee"
}
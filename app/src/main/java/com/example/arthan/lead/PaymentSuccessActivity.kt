package com.example.arthan.lead

import android.content.Intent
import android.widget.Toast
import com.example.arthan.R
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.global.AppPreferences
import com.example.arthan.model.ELIGIBILITY_SCREEN
import com.example.arthan.model.PAYMENT_SCREEN
import com.example.arthan.model.UpdateEligibilityAndPaymentReq
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_payment_success.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import com.example.arthan.utils.ArgumentKey


class PaymentSuccessActivity: BaseActivity() {

    override fun contentView()= R.layout.activity_payment_success

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        btn_next.setOnClickListener {

            val progressBar = ProgrssLoader(this)
            progressBar.showLoading()

            val leadId= AppPreferences.getInstance().getString(ArgumentKey.LeadId)
            val loanId= AppPreferences.getInstance().getString(AppPreferences.Key.LoanId)

            CoroutineScope(Dispatchers.IO).launch {

                try {

                    val response =
                        RetrofitFactory.getApiService().updateEligibilityAndPayment(
                            UpdateEligibilityAndPaymentReq(
                                leadId,
                                if (loanId.isNullOrBlank()) "C1234" else loanId, PAYMENT_SCREEN
                            )
                        )

                    if (response.isSuccessful && response.body() != null) {

                        if (response.body()?.apiCode == "200") {

                            withContext(Dispatchers.Main) {
                                progressBar.dismmissLoading()
                                if (response.body()?.eligibility.equals("N", ignoreCase = true)) {
                                    startActivity(
                                        Intent(
                                            this@PaymentSuccessActivity,
                                            RMDashboardActivity::class.java
                                        )
                                    )
                                    finish()
                                } else {
                                    startActivity(
                                        Intent(
                                            this@PaymentSuccessActivity,
                                            LeadInfoCaptureActivity::class.java
                                        ).apply {
                                            putExtra("annualturnover",response.body()!!.annualTurnover)
                                            putExtra("businessName",response.body()!!.businessName)
                                        }
                                    )
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                progressBar.dismmissLoading()
                                Toast.makeText(
                                    this@PaymentSuccessActivity,
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
                            this@PaymentSuccessActivity,
                            "Something went wrong.Please try again..",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }

        }

    }

    override fun screenTitle()= "Payment Success"
}
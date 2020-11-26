package com.example.arthan.lead

import android.content.Intent
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.example.arthan.R
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.dashboard.rm.RMScreeningNavigationActivity
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

            var leadId= AppPreferences.getInstance().getString(ArgumentKey.LeadId)
            var loanId= AppPreferences.getInstance().getString(AppPreferences.Key.LoanId)

            if(loanId!="")
            {
                loanId=intent.getStringExtra("loanId")
                leadId=intent.getStringExtra("leadId")
            }
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

                                if (intent.getStringExtra("task") == "RMreJourney") {
                                    withContext(Dispatchers.Main) {

                                        startActivity(
                                            Intent(
                                                this@PaymentSuccessActivity,
                                                RMScreeningNavigationActivity::class.java
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
                                            this@PaymentSuccessActivity,
                                            "We are sorry, your Credit score is low",
                                            Toast.LENGTH_LONG
                                        ).show()
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
                                    this@PaymentSuccessActivity,
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
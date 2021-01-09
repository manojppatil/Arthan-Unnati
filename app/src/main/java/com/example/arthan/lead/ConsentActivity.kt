package com.example.arthan.lead

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.arthan.R
import com.example.arthan.dashboard.rm.RMScreeningNavigationActivity
import com.example.arthan.global.AppPreferences
import com.example.arthan.global.Crashlytics
import com.example.arthan.model.ELIGIBILITY_SCREEN
import com.example.arthan.model.MarkConsentRequest
import com.example.arthan.model.UpdateEligibilityAndPaymentReq
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_consent.*
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.ProgrssLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class ConsentActivity : BaseActivity() {

    override fun screenTitle() = "In Principle Amount"

    override fun contentView() = R.layout.activity_consent

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        btn_next.isEnabled = true

        val messageText = "Your in principle amount is : "
        val spannableStringBuilder = SpannableStringBuilder(
            "$messageText${intent.getStringExtra(ArgumentKey.InPrincipleAmount)}"
        )
        spannableStringBuilder.setSpan(
            StyleSpan(Typeface.BOLD),
            messageText.length,
            spannableStringBuilder.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        in_principle_amount_text?.text = spannableStringBuilder

        /*chk_consent.setOnCheckedChangeListener { _, isChecked ->
            btn_next.isEnabled = isChecked
        }*/

        btn_next.setOnClickListener {

            val progressBar = ProgrssLoader(this)
            progressBar.showLoading()

            val leadId = AppPreferences.getInstance().getString(ArgumentKey.LeadId)
            val loanId = intent.getStringExtra("loanId")
            val customerID = intent.getStringExtra("custId")

            CoroutineScope(Dispatchers.IO).launch {

                try {
                    val map=HashMap<String,String>()
                    map["customerId"]=customerID
                    map["loanId"]=loanId

                    val response =
                        RetrofitFactory.getApiService().acceptInPrincipalAmt(
                            map
                           /* MarkConsentRequest(
                                leadId,
                                if (loanId.isNullOrBlank()) "C1234" else loanId,
                                if (customerID.isNullOrBlank()) "" else customerID,
                                if (chk_consent.isChecked) "Yes" else "No"
                            )*/
                        )

                    if (response.isSuccessful && response.body() != null) {

                        if (response.body()?.apiCode == "200") {

                            if (intent.getStringExtra("task") == "RMreJourney") {
                                withContext(Dispatchers.Main) {

                                    startActivity(
                                        Intent(
                                            this@ConsentActivity,
                                            RMScreeningNavigationActivity::class.java
                                        ).apply {
                                            putExtra("loanId", loanId)
                                        }
                                    )
                                    finish()
                                }

                            }else {
                                withContext(Dispatchers.Main) {
                                    progressBar.dismmissLoading()
                                    startActivity(
                                        Intent(
                                            this@ConsentActivity,
                                            OTPValidationActivity::class.java
                                        ).apply {
                                            putExtra("loanId", response.body()!!.loanId)
                                            putExtra("leadId", response.body()!!.leadId)
                                            putExtra("custId", response.body()!!.customerId)
                                            putExtra("mobNo", response.body()!!.mobNo)
                                            putExtra("appFee", response.body()!!.appFee)
                                            putExtra("gst", response.body()!!.gst)
                                            putExtra("total", response.body()!!.total)
                                        }
                                    )
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                progressBar.dismmissLoading()
                                Toast.makeText(
                                    this@ConsentActivity,
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
                            this@ConsentActivity,
                            "Something went wrong.Please try again..",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }


        }

    }

    companion object {
        fun startMe(context: Context?, principleAmount: String?,custId:String?,loanId:String?) =
            context?.startActivity(Intent(context, ConsentActivity::class.java).apply {
                putExtra(ArgumentKey.InPrincipleAmount, principleAmount)
                putExtra("custId", custId)
                putExtra("loanId", loanId)
            })
    }
}
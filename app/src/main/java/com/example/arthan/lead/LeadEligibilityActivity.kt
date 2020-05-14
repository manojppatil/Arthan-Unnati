package com.example.arthan.lead

import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.example.arthan.R
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.global.AppPreferences
import com.example.arthan.model.ELIGIBILITY_SCREEN
import com.example.arthan.model.UpdateEligibilityAndPaymentReq
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_loan_eligibility.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.SplashActivity
import kotlinx.coroutines.withContext
import java.lang.Exception

class LeadEligibilityActivity : BaseActivity() {

    override fun contentView() = R.layout.activity_loan_eligibility

    override fun onToolbarBackPressed() = onBackPressed()

    private var isEligible=true
    override fun init() {

        val eligibility=intent.getStringExtra("Eligibility")
        if(eligibility!=null)
        {
            isEligible = eligibility.equals("y",ignoreCase = true)

        }
        if(!isEligible)
        {
         txt_msg.text="Sorry, You are not eligible for Loan"
        }
        btn_ok.setOnClickListener {


            val progressBar = ProgrssLoader(this)
            progressBar.showLoading()

            val leadId= intent.getStringExtra(ArgumentKey.LeadId)
            val loanId= AppPreferences.getInstance().getString(AppPreferences.Key.LoanId)

            CoroutineScope(Dispatchers.IO).launch {

                try {

                    val response =
                        RetrofitFactory.getApiService().updateEligibilityAndPaymentInitiate(
                            UpdateEligibilityAndPaymentReq(
                                leadId,
                                if (loanId.isNullOrBlank()) "C1234" else loanId, ELIGIBILITY_SCREEN
                            )
                        )

                    if (response.isSuccessful && response.body() != null) {
                        if (response.body()?.apiCode == "200") {

                            withContext(Dispatchers.Main) {
                                progressBar.dismmissLoading()
//                                if(response.body()?.eligibility.equals("Y",ignoreCase = true)) {
                                if(isEligible) {
                                    startActivity(
                                        Intent(
                                            this@LeadEligibilityActivity,
                                            AddLeadStep2Activity::class.java
                                        )
                                    )
                                    finish()
                                }else{
                                    startActivity(Intent(this@LeadEligibilityActivity,RMDashboardActivity::class.java))
                                    finish()
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                progressBar.dismmissLoading()
                                Toast.makeText(
                                    this@LeadEligibilityActivity,
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
                            this@LeadEligibilityActivity,
                            "Something went wrong.Please try again..",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
    }

    override fun screenTitle() = "Eligibility"

    companion object {
        fun startMe(context: Context?, leadId: String,eligibility:String?) =

            context?.startActivity(Intent(context, LeadEligibilityActivity::class.java).apply {
                putExtra(ArgumentKey.LeadId,leadId)
                putExtra(ArgumentKey.Eligibility,"N")
            })
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {


        menuInflater.inflate(R.menu.more,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.homeMenu->{
                finish()

            }
            R.id.logoutMenu->
            {
                finish()
                startActivity(Intent(this, SplashActivity::class.java))
            }

        }
        return super.onOptionsItemSelected(item)
    }
}
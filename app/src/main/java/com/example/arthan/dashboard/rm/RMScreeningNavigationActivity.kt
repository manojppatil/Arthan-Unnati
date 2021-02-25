
package com.example.arthan.dashboard.rm

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.arthan.R
import com.example.arthan.dashboard.rm.adapters.RMScreeningNavAdapter
import com.example.arthan.lead.*
import com.example.arthan.model.ScreeningNavDataResponse
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.ProgrssLoader
import kotlinx.android.synthetic.main.activity_r_m_screening_navigation.*
import kotlinx.android.synthetic.main.screen_nav_adapter_row.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RMScreeningNavigationActivity : AppCompatActivity() {

    private lateinit var responseGlobal: ScreeningNavDataResponse
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_r_m_screening_navigation)
        //   back_button.setOnClickListener { onBackPressed() }
//        toolbar_title.text = "Complete Loan Details"
        getLoanData()
        addnewApplicant.setOnClickListener {
            val dialog=AlertDialog.Builder(this)
            dialog.setTitle("Add new Applicant")
            dialog.setMessage("Select the type of Applicant you want to Add")
            dialog.setNegativeButton("Guarantor", DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
                startActivity(Intent(this, AddLeadStep2Activity::class.java).apply {
                    putExtra("screen","KYC_PA")
                    putExtra("type","G")
                    putExtra("loanId",responseGlobal.loanId)
                    putExtra("custId",responseGlobal.customerId)
                    putExtra("task","RMreJourney")
                })
                finish()
            })
            dialog.setPositiveButton("Co-Applicant", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                startActivity(Intent(this, AddLeadStep2Activity::class.java).apply {
                    putExtra("screen","KYC_PA")
                    putExtra("type","CA")
                    putExtra("loanId",responseGlobal.loanId)
                    putExtra("custId",responseGlobal.customerId)
                    putExtra("task","RMreJourney")
                })
                finish()
            })
            dialog.setNeutralButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            dialog.create().show()
        }
        continueScreen.setOnClickListener {

            moveScreen(screenValue.text.toString())
        }

    }

    private fun moveScreen(screen: String) {
        when (screen) {
            "LOAN"->{
                startActivity(Intent(this, LoanDetailActivity::class.java).apply {
                    putExtra("screen","LOAN")
                    putExtra("loanId",responseGlobal.loanId)
                    putExtra("leadId",responseGlobal.leadId)
                    putExtra("custId",responseGlobal.customerId)
                    putExtra("task","RMContinue")
                })
               finish()
            }
            "KYC_PA"->{
                startActivity(Intent(this, AddLeadStep2Activity::class.java).apply {
                    putExtra("screen","KYC_PA")
                    putExtra("loanId",responseGlobal.loanId)
                    putExtra("custId",responseGlobal.customerId)
                    putExtra("task","RMContinue")
                })
                finish()

            }
            "KYC_CA"->{
                startActivity(Intent(this, AddLeadStep2Activity::class.java).apply {
                    putExtra("screen","KYC_CA")
                    putExtra("loanId",responseGlobal.loanId)
                    putExtra("custId",responseGlobal.customerId)
                    putExtra("task","RMContinue")
                })
                finish()

            }
            "KYC_CA1"->{
                startActivity(Intent(this, AddLeadStep2Activity::class.java).apply {
                    putExtra("screen","KYC_CA1")
                    putExtra("loanId",responseGlobal.loanId)
                    putExtra("custId",responseGlobal.customerId)
                    putExtra("task","RMContinue")
                })
                finish()

            }
            "KYC_CA2"->{
                startActivity(Intent(this, AddLeadStep2Activity::class.java).apply {
                    putExtra("screen","KYC_CA2")
                    putExtra("loanId",responseGlobal.loanId)
                    putExtra("custId",responseGlobal.customerId)
                    putExtra("task","RMContinue")
                })
                finish()

            }
            "KYC_CA3"->{
                startActivity(Intent(this, AddLeadStep2Activity::class.java).apply {
                    putExtra("screen","KYC_CA3")
                    putExtra("loanId",responseGlobal.loanId)
                    putExtra("custId",responseGlobal.customerId)
                    putExtra("task","RMContinue")
                })
                finish()

            }
            "KYC_CA4"->{
                startActivity(Intent(this, AddLeadStep2Activity::class.java).apply {
                    putExtra("screen","KYC_CA4")
                    putExtra("loanId",responseGlobal.loanId)
                    putExtra("custId",responseGlobal.customerId)
                    putExtra("task","RMContinue")
                })
                finish()

            }
            "KYC_CA5"->{
                startActivity(Intent(this, AddLeadStep2Activity::class.java).apply {
                    putExtra("screen","KYC_CA5")
                    putExtra("loanId",responseGlobal.loanId)
                    putExtra("custId",responseGlobal.customerId)
                    putExtra("task","RMContinue")
                })
                finish()

            }
            "CONSENT"->{
                startActivity(Intent(this, ConsentActivity::class.java).apply {
                    putExtra("screen","CONSENT")
                    putExtra("loanId",responseGlobal.loanId)
                    putExtra("custId",responseGlobal.customerId)
                    putExtra(ArgumentKey.InPrincipleAmount,responseGlobal.inPrincpAmt)
                    putExtra("task","RMContinue")
                })
                finish()

            }
             "DOCUMENTS"->{
                startActivity(Intent(this, DocumentActivity::class.java).apply {
                    putExtra("screen","DOCUMENTS")
                    putExtra("loanId",responseGlobal.loanId)
                    putExtra("custId",responseGlobal.customerId)
                    putExtra("task","RMContinue")
                })
                finish()

            }
            "PAYMENT"->{
//                startActivity(Intent(this, PaymentSuccessActivity::class.java).apply {
                startActivity(Intent(this, PaymentQRActivity::class.java).apply {
                    putExtra("screen","CONSENT")
                    putExtra("loanId",responseGlobal.loanId)
                    putExtra("custId",responseGlobal.customerId)
                    putExtra(ArgumentKey.InPrincipleAmount,responseGlobal.inPrincpAmt)
                    putExtra("task","RMContinue")
                })
              finish()

            }

            "ELIGIBILITY"->{
                startActivity(
                    Intent(
                        this,
                        LeadEligibilityActivity::class.java
                    ).apply {

                        putExtra("loanId",responseGlobal.loanId)
                        putExtra("custId",responseGlobal.customerId)
                        putExtra("leadId",responseGlobal.leadId)

                        putExtra("screen", "eligibility")
                        putExtra("task", "RMContinue")
                    }
                )
                finish()

            }
            "BUSINESS" -> {
              /*  startActivity(Intent(this, AddLeadActivity::class.java).apply {
                    putExtra("screen", "business")
                    putExtra("loanId", responseGlobal.loanId)
                    putExtra("custId", responseGlobal.customerId)
                    putExtra("task", "RMContinue")
                })
               finish()*/
                startActivity(
                    Intent(
                        this,
                        LeadInfoCaptureActivity::class.java
                    ).apply {

                        putExtra("loanId",responseGlobal.loanId)
                        putExtra("custId",responseGlobal.customerId)
                        putExtra("screen", "business")
                        putExtra("task", "RMContinue")
                    }
                )
                finish()


            }
            "INCOME" -> {
               /* startActivity(Intent(this, AddLeadActivity::class.java).apply {
                    putExtra("screen", "income")
                    putExtra("loanId", responseGlobal.loanId)
                    putExtra("custId", responseGlobal.customerId)
                    putExtra("task", "RMContinue")
                })
                finish()*/
                startActivity(
                    Intent(
                        this,
                        LeadInfoCaptureActivity::class.java
                    ).apply {

                        putExtra("loanId",responseGlobal.loanId)
                        putExtra("custId",responseGlobal.customerId)
                        putExtra("screen", "income")
                        putExtra("task", "RMContinue")
                    }
                )
                finish()


            }
            "PERSONAL_PA"->{
                startActivity(Intent(this, PersonalInformationActivity::class.java).apply {
                    putExtra("screen","PERSONAL_PA")
                    putExtra("loanId",responseGlobal.loanId)
                    putExtra("custId",responseGlobal.customerId)
                    putExtra("task","RMContinue")
                })
                (this as RMScreeningNavigationActivity).finish()
            }
            "PERSONAL_CA"->{
                startActivity(Intent(this, PersonalInformationActivity::class.java).apply {
                    putExtra("screen","PERSONAL_CA")
                    putExtra("loanId",responseGlobal.loanId)
                    putExtra("custId",responseGlobal.customerId)
                    putExtra("task","RMContinue")
                })
                (this as RMScreeningNavigationActivity).finish()
            }

            "PERSONAL_CA1"->{
                startActivity(Intent(this, PersonalInformationActivity::class.java).apply {
                    putExtra("screen","PERSONAL_CA1")
                    putExtra("loanId",responseGlobal.loanId)
                    putExtra("custId",responseGlobal.customerId1)
                    putExtra("task","RMContinue")
                })
                (this as RMScreeningNavigationActivity).finish()
            }
            "PERSONAL_CA2"->{
                startActivity(Intent(this, PersonalInformationActivity::class.java).apply {
                    putExtra("screen","PERSONAL_CA2")
                    putExtra("loanId",responseGlobal.loanId)
                    putExtra("custId",responseGlobal.customerId2)
                    putExtra("task","RMContinue")
                })
                (this as RMScreeningNavigationActivity).finish()
            }
            "PERSONAL_CA3"->{
                startActivity(Intent(this, PersonalInformationActivity::class.java).apply {
                    putExtra("screen","PERSONAL_CA3")
                    putExtra("loanId",responseGlobal.loanId)
                    putExtra("custId",responseGlobal.customerId3)
                    putExtra("task","RMContinue")
                })
                (this as RMScreeningNavigationActivity).finish()
            }
            "PERSONAL_CA4"->{
                startActivity(Intent(this, PersonalInformationActivity::class.java).apply {
                    putExtra("screen","PERSONAL_CA4")
                    putExtra("loanId",responseGlobal.loanId)
                    putExtra("custId",responseGlobal.customerId4)
                    putExtra("task","RMContinue")
                })
                (this as RMScreeningNavigationActivity).finish()
            }
            "PERSONAL_CA5"->{
                startActivity(Intent(this, PersonalInformationActivity::class.java).apply {
                    putExtra("screen","PERSONAL_CA5")
                    putExtra("loanId",responseGlobal.loanId)
                    putExtra("custId",responseGlobal.customerId5)
                    putExtra("task","RMContinue")
                })
                (this as RMScreeningNavigationActivity).finish()
            }
            "PERSONAL_G"->{
                startActivity(Intent(this, PersonalInformationActivity::class.java).apply {
                    putExtra("screen","PERSONAL_G")
                    putExtra("loanId",responseGlobal.loanId)
                    putExtra("custId",responseGlobal.customerId6)
                    putExtra("task","RMContinue")
                })
                (this as RMScreeningNavigationActivity).finish()
            }
            "OTHERS","OTHERS_TRADE","OTHERS_SECURITY" -> {
               /* startActivity(Intent(this, AddLeadActivity::class.java).apply {
                    putExtra("screen", "others")
                    putExtra("loanId", responseGlobal.loanId)
                    putExtra("custId", responseGlobal.customerId)
                    putExtra("task", "RMContinue")
                })
                finish()*/
                startActivity(
                    Intent(
                        this,
                        LeadInfoCaptureActivity::class.java
                    ).apply {

                        putExtra("loanId",responseGlobal.loanId)
                        putExtra("custId",responseGlobal.customerId)
                        putExtra("screen", "others")
                        putExtra("task", "RMContinue")
                    }
                )
                finish()


            }
            "OTP","OTP_CONSENT"->{
                startActivity(Intent(this, OTPValidationActivity::class.java).apply {
                    putExtra("screen","OTP")
                    putExtra("loanId",responseGlobal.loanId)
                    putExtra("custId",responseGlobal.customerId)
                    putExtra("gst",responseGlobal.gst)
                    putExtra("total",responseGlobal.total)
                    putExtra("appFee",responseGlobal.appFee)
                    putExtra("leadId",responseGlobal.leadId)
                    putExtra("mobNo",responseGlobal.mobNo)
                    putExtra("task","RMContinue")
                })
                (this as RMScreeningNavigationActivity).finish()

            }

            "APPFEE"->{
                startActivity(Intent(this, ApplicationFeeActivity::class.java).apply {
                    putExtra("screen","APPFEE")
                    putExtra("loanId",responseGlobal.loanId)
                    putExtra("custId",responseGlobal.customerId)
                    putExtra("task","RMContinue")
                })
               finish()

            }
        }
    }


        private fun getLoanData() {

        val loader=ProgrssLoader(this)
        loader.showLoading()
            var loanOrLeadId=""
            loanOrLeadId = if(intent.getStringExtra("loanId")!=null&&intent.getStringExtra("loanId")!="") {
                intent.getStringExtra("loanId")
            }else {
                intent.getStringExtra("leadId")
            }
        CoroutineScope(Dispatchers.IO).launch {

            val response =
                RetrofitFactory.getApiService().getLoanDataStatus(loanOrLeadId)

            if (response?.body() != null) {
                withContext(Dispatchers.Main) {
                    loader.dismmissLoading()
                    responseGlobal=response.body()!!
                    if(response.body()?.continueScreen!!.isNotEmpty())
                    {
                       // responseGlobal=response.body()!!
                        continueScreen.visibility=View.VISIBLE
                        status.setImageResource(R.drawable.error)
                        screenValue.text=response.body()?.continueScreen
                    }
                    rvScreeningList.adapter =
                        RMScreeningNavAdapter(this@RMScreeningNavigationActivity, "ReScreen", response.body())
                }
            }else
            {
                loader.dismmissLoading()

            }
        }
    }
}

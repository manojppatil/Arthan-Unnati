package com.example.arthan.lead

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import com.example.arthan.R
import com.example.arthan.dashboard.bcm.BCMDashboardActivity
import com.example.arthan.dashboard.bm.BMDashboardActivity
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.global.*
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_lead_info_capture.*

class LeadInfoCaptureActivity: BaseActivity() {

    var panData: String= ""

    override fun screenTitle()= "Complete Details"

    override fun contentView()= R.layout.activity_lead_info_capture

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {
        if(intent.hasExtra("PAN_DATA"))
            panData= intent.getStringExtra("PAN_DATA")

        if(intent.getStringExtra("screen")!=null)
        {
            moveToScreen(intent.getStringExtra("screen")!!)

        }

    }
    private fun moveToScreen(screenId: String) {

        // var screenId="others"
        var navController= Navigation.findNavController(
        this,
        R.id.frag_container
        )
        if(screenId.equals("loan",ignoreCase = true))
        {
            navController.popBackStack(R.id.addLoanDetailsFragment, true);

            navController.navigate(R.id.addLoanDetailsFragment)
        }
        else if(screenId.equals("eligibility",ignoreCase = true))
        {
            /* navController.popBackStack(R.id.loanEligibilityFragment, true);

             navController.navigate(R.id.loanEligibilityFragment)*/
            startActivity(Intent(this, LeadEligibilityActivity::class.java).apply {
                putExtra("loanId",intent.getStringExtra("loanId"))
                putExtra("custId",intent.getStringExtra("custId"))
                putExtra(ArgumentKey.Eligibility,"y")//hard coded @@
            })
            finish()
        }
        else if(screenId.equals("kyc",ignoreCase = true))
        {
            navController.popBackStack(R.id.addKYCDetailsFragment, true);

            navController.navigate(R.id.addKYCDetailsFragment)
        }
        else if(screenId.equals("consent",ignoreCase = true))
        {
            /*navController.popBackStack(R.id.approveConsentFragment, true);

            navController.navigate(R.id.approveConsentFragment)*/
            startActivity(Intent(this,ConsentActivity::class.java))
        }
        else if(screenId.equals("otp",ignoreCase = true))
        {
            navController.popBackStack(R.id.OTPValidationFragment, true);

            navController.navigate(R.id.OTPValidationFragment)
        }
        else if(screenId.equals("AppFee",ignoreCase = true))
        {
            navController.popBackStack(R.id.applicationFeePaymentFragment, true);

            navController.navigate(R.id.applicationFeePaymentFragment)
        }
        else if(screenId.equals("paymentFinal",ignoreCase = true))
        {
            navController.popBackStack(R.id.paymentStatusFragment, true);

            navController.navigate(R.id.paymentStatusFragment)
        }
        else if(screenId.equals("business",ignoreCase = true))
        {
            navController.popBackStack(R.id.completeDetailsFragment, true);
            var b=Bundle()
            b.putString("loanId",intent.getStringExtra("loanId"))
            b.putString("custId",intent.getStringExtra("custId"))
//            b.putString("task",intent.getStringExtra("task"))
            b.putString("screenTo",screenId)
            navController.navigate(R.id.frag_business_info,b)
            infoCompleteState(BUSINESS)
            vw_dim_income.visibility= View.VISIBLE
            navController?.navigate(R.id.frag_business_info,b)
            infoInCompleteState(BUSINESS)

        }
        else if(screenId.equals("income",ignoreCase = true))
        {
            navController.popBackStack(R.id.completeDetailsFragment, true);

            var b=Bundle()
            b.putString("loanId",intent.getStringExtra("loanId"))
            b.putString("custId",intent.getStringExtra("custId"))
//            b.putString("task",intent.getStringExtra("task"))
            b.putString("screenTo",screenId)
            vw_dim_doc.visibility= View.VISIBLE
            enableInCome()
//                infoCompleteState(INCOME)
            infoInCompleteState(INCOME)
            navController?.navigate(R.id.frag_income_info,b)
        }
        else if(screenId.equals("others",ignoreCase = true))
        {
            var b=Bundle()
            b.putString("loanId",intent.getStringExtra("loanId"))
            b.putString("custId",intent.getStringExtra("custId"))
//            b.putString("task",intent.getStringExtra("task"))
            b.putString("screenTo",screenId)
            navController?.navigate(R.id.frag_document_info,b)
            enableDoc()
            enableInCome()
//                infoCompleteState(INCOME)
            infoInCompleteState(DOCUMENT)
        }

        else if(screenId.equals("documents",ignoreCase = true))
        {
            startActivity(Intent(this,DocumentActivity::class.java).apply {
                putExtra("loanId",intent.getStringExtra("loanId"))
                putExtra("custId",intent.getStringExtra("custId"))
            })
            /* var b=Bundle()
             b.putString("screenTo",screenId)
             navController.popBackStack(R.id.completeDetailsFragment, true);

             navController.navigate(R.id.completeDetailsFragment,b)*/
        }

        else if(screenId.equals("complete",ignoreCase = true))
        {
            navController.popBackStack(R.id.completeDetailsFragment, true);

            navController.navigate(R.id.completeDetailsFragment)
        }

    }

    fun infoCompleteState(type: String){
        when (type) {
            DOCUMENT -> {
                txt_doc.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_info_completed,0,0)
            }
            BUSINESS -> {
                txt_Business.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_info_completed,0,0)
            }
            INCOME -> {
                txt_income.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_info_completed,0,0)
            }
        }
    }
    fun infoInCompleteState(type: String){
        when (type) {
            DOCUMENT -> {
                txt_doc.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_personal_info,0,0)
            }
            BUSINESS -> {
                txt_Business.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_business_info,0,0)
            }
            INCOME -> {
                txt_income.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_income_info,0,0)
            }
        }
    }

    override fun onBackPressed() {
        val navController: NavController? = Navigation.findNavController(
            this,
            R.id.frag_container
        )
        when((navController?.currentDestination as FragmentNavigator.Destination).className)
        {
          "com.example.arthan.lead.OtherDetailsFragment"->
            {

                var b= Bundle()
                b.putString("from","rmIncome")
                b.putString("loanId",intent.getStringExtra("loanId"))
                b.putString("custId",intent.getStringExtra("custId"))
                if(navController.popBackStack(R.id.frag_income_info, false)) {

                    navController.popBackStack(R.id.frag_income_info,false)
                }else {
                    navController?.navigate(R.id.frag_income_info, b)
                }
                vw_dim_doc.visibility= View.VISIBLE
                enableInCome()
//                infoCompleteState(INCOME)
                infoInCompleteState(INCOME)
                //income
               // finish()
            }
            "com.example.arthan.lead.IncomeInformationFragment"->{
                enableBusiness()
                var b= Bundle()
                b.putString("loanId",intent.getStringExtra("loanId"))
                b.putString("customerId",intent.getStringExtra("custId"))
                infoCompleteState(BUSINESS)
                vw_dim_income.visibility= View.VISIBLE
                if(navController.popBackStack(R.id.frag_business_info, false)) {

                    navController.popBackStack(R.id.frag_business_info,false)
                }else {
                    navController?.navigate(R.id.frag_business_info, b)
                }
                infoInCompleteState(BUSINESS)


            }
           else->
        {

            finish()

        }
        }
    }

    fun enableBusiness(){
        vw_dim_business.visibility= View.GONE
    }

    fun enableInCome(){
        vw_dim_income.visibility= View.GONE
    }

    fun enableDoc(){
        vw_dim_doc.visibility= View.GONE
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {


        menuInflater.inflate(R.menu.more,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.homeMenu->{
                finish()
                if(ArthanApp.getAppInstance().loginRole=="RM")
                {
                    startActivity(Intent(this, RMDashboardActivity::class.java))
                }else if(ArthanApp.getAppInstance().loginRole=="BCM")
                {
                    startActivity(Intent(this, BCMDashboardActivity::class.java))

                }else if(ArthanApp.getAppInstance().loginRole=="BM")
                {
                    startActivity(Intent(this, BMDashboardActivity::class.java))

                }
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
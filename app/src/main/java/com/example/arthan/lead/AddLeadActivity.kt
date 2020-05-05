package com.example.arthan.lead

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.EventLog
import android.view.KeyEvent
import androidx.navigation.Navigation
import com.example.arthan.R
import com.example.arthan.dashboard.rm.RMScreeningListingActivity
import com.example.arthan.utils.ArgumentKey

class AddLeadActivity : AppCompatActivity() {

    private var isFromNavigation=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_lead)

        if(intent.getStringExtra("screen")!=null)
        {
            moveToScreen(intent.getStringExtra("screen")!!)

        }

    }
    private fun moveToScreen(screenId: String) {

       // var screenId="others"
        isFromNavigation=true
        var navController=Navigation.findNavController(
            this,
            R.id.nav_host_fragment_container
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

            navController.navigate(R.id.completeDetailsFragment)
        }
        else if(screenId.equals("income",ignoreCase = true))
        {
            navController.popBackStack(R.id.completeDetailsFragment, true);

            var b=Bundle()
            b.putString("screenTo",screenId)
            navController.navigate(R.id.completeDetailsFragment,b)
        }
        else if(screenId.equals("others",ignoreCase = true))
        {
            var b=Bundle()
            b.putString("screenTo",screenId)
            navController.popBackStack(R.id.completeDetailsFragment,true)
            navController.navigate(R.id.completeDetailsFragment,b)
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if(keyCode==KeyEvent.KEYCODE_BACK) {
            if (isFromNavigation )
                finish()
        }
        return super.onKeyDown(keyCode, event)
    }

}

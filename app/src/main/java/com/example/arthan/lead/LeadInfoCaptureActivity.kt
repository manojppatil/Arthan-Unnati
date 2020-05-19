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
import com.example.arthan.global.BUSINESS
import com.example.arthan.global.DOCUMENT
import com.example.arthan.global.INCOME
import com.example.arthan.global.PERSONAL
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
                navController?.navigate(R.id.frag_income_info,b)
                vw_dim_doc.visibility= View.VISIBLE
                enableInCome()
//                infoCompleteState(INCOME)
                infoInCompleteState(INCOME)
                //income
               // finish()
            }
            "com.example.arthan.lead.IncomeInformationFragment"->{
                enableBusiness()
                infoCompleteState(BUSINESS)
                vw_dim_income.visibility= View.VISIBLE
                navController?.navigate(R.id.frag_business_info)
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
package com.example.arthan.dashboard.bm

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.arthan.R
import com.example.arthan.dashboard.bcm.BCMDashboardActivity
import com.example.arthan.dashboard.rm.CommonApprovedListingActivity
import com.example.arthan.global.ArthanApp
import com.example.arthan.model.ApprovedCaseData
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.PendingCustomersActivity
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_approved_customer_review.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LPCActivity : BaseActivity() {

    override fun contentView() = R.layout.activity_approved_customer_review

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        btn_search.visibility = View.GONE
        btn_filter.visibility = View.GONE
        setupUI()

        var data=intent.extras?.get("object") as ApprovedCaseData
        tv_loan_id_value.text=data.caseId
        tv_loan_name_value.text=data.name
        et_loan_amt.setText("₹"+data.approvedAmt)
        et_tenure.setText(data.tenure)
        et_rate_of_interest.setText("%"+data.roi)
        et_processing_fees.setText(data.pf)
        et_insurance.setText(data.insurance)

        btn_submit.setOnClickListener {

            val progressBar = ProgrssLoader(this)
            progressBar.showLoading()
            var loanAmtOld=data.approvedAmt
            var tenureOld=data.tenure
            var roiOld=data.roi
            var pfOld=data.pf
            var insurance=data.insurance

            var loanAmtNew=""
            var tenureNew=""
            var roiNew=""
            var pfNew=""
            var insuranceNew=""
            loanAmtNew=et_loan_amt.text.toString().replace("₹","")
            tenureNew=et_tenure.text.toString()
            roiNew=et_rate_of_interest.text.toString().replace("%","")
            pfNew=et_processing_fees.text.toString()
            insuranceNew=et_insurance.text.toString()

            val map=HashMap<String,String>()
                map["loanId"]=data.caseId
                map["loanAmtOld"]=loanAmtOld
                map["loanAmtNew"]=loanAmtNew
                map["tenureOld"]=tenureOld
                map["tenureNew"]=tenureNew
                map["roiOld"]=roiOld
                map["roiNew"]=roiNew
                map["pfOld"]=pfOld
                map["pfNew"]=pfNew
                map["insuranceOld"]=insurance
                map["insuranceNew"]=insuranceNew

            CoroutineScope(Dispatchers.IO).launch {

                val res=RetrofitFactory.getApiService().submitLPC(map)
                if(res?.body()!= null)
                {

                    withContext(Dispatchers.Main) {
                        progressBar.dismmissLoading()
                        if (res.body()!!.eligibility!!.equals( "y",ignoreCase = true)) {
                            var toMoveToQueueORApproved = checkForScreenNavigations()
                            if (toMoveToQueueORApproved.contentEquals("next")) {
                                startActivity(
                                    Intent(
                                        this@LPCActivity,
                                        PostSanctionDocActivity::class.java
                                    ).apply {
                                        putExtra("FROM", ArthanApp.getAppInstance().loginRole)
                                        putExtra("Name", intent.getStringExtra("Name"))
                                    })
                            }
                            if (toMoveToQueueORApproved.contentEquals("queue")) {
                                startActivity(
                                    Intent(
                                        this@LPCActivity,
                                        PendingCustomersActivity::class.java
                                    ).apply {
                                        putExtra("FROM", ArthanApp.getAppInstance().loginRole)
                                        putExtra("Name", intent.getStringExtra("Name"))
                                    })
                            }
                            if (toMoveToQueueORApproved.contentEquals("approve")) {
                                startActivity(
                                    Intent(
                                        this@LPCActivity,
                                        BCMDashboardActivity::class.java
                                    ).apply {
                                        putExtra("FROM",ArthanApp.getAppInstance().loginRole)
                                        putExtra("Name", intent.getStringExtra("Name"))
                                    })
                            }
                            if (toMoveToQueueORApproved.contentEquals("ApprovedListBCM")) {
                                startActivity(
                                    Intent(
                                        this@LPCActivity,
                                        CommonApprovedListingActivity::class.java
                                    ).apply {
                                        putExtra("FROM",ArthanApp.getAppInstance().loginRole)
                                        putExtra("Name", intent.getStringExtra("Name"))
                                    })
                            }
                        } else {
                           startActivity( Intent(
                                this@LPCActivity,
                                CommonApprovedListingActivity::class.java
                            ).apply {
                                putExtra("FROM", ArthanApp.getAppInstance().loginRole)
                            })
                            finish()

                        }
                    }
                }else
                {
                    progressBar.dismmissLoading()

                }
            }


            /*startActivity(
                Intent(
                    this@LPCActivity,
                    ApprovedCustomerLegalStatusActivity::class.java
                ).apply {
                    putExtra("FROM", intent.getStringExtra("FROM"))
                })*/
        }
    }

    private fun checkForScreenNavigations(): String {

        var data=intent.getSerializableExtra("object") as ApprovedCaseData
        if(ArthanApp.getAppInstance().loginRole.contentEquals("BM"))
        {
            var dataChanged="next"
            if(!et_loan_amt.text.toString().contentEquals(data.approvedAmt.replace("₹","")))
            {
                dataChanged="queue"
                return dataChanged
            }
            if(!et_tenure.text.toString().contentEquals(data.tenure)){
                dataChanged="queue"
                return dataChanged
            }
            return dataChanged
        }
        if(ArthanApp.getAppInstance().loginRole.contentEquals("BCM"))
        {

            if(!et_loan_amt.text.toString().replace("₹","").contentEquals(data.approvedAmt)||!et_tenure.text.toString().contentEquals(data.tenure))
            {
                return "ApprovedListBCM"
            }
           else if(et_loan_amt.text.toString().replace("₹","").contentEquals(data.approvedAmt)&&et_tenure.text.toString().contentEquals(data.tenure)){

                if(!et_rate_of_interest.text.toString().replace("%","").contentEquals(data.roi)||!et_processing_fees.text.toString().contentEquals(data.pf)||
                        !et_insurance.text.toString().contentEquals(data.insurance)) {
                    return "ApprovedListBCM"
                }
            }else
            {
                return "next"
            }


            return "next"
        }


        return "next"
    }

    private fun setupUI() {





        if(ArthanApp.getAppInstance().loginRole.contentEquals("BM"))
        {
            btn_edit.visibility=View.VISIBLE
            btn_tenure_edit.visibility=View.VISIBLE
            btn_insurance_edit.visibility=View.INVISIBLE
            btn_processing_fees_edit.visibility=View.INVISIBLE
            btn_rate_of_interest_edit.visibility=View.INVISIBLE
            et_loan_amt.isFocusableInTouchMode=true
            et_tenure.isFocusableInTouchMode=true
            et_rate_of_interest.isFocusableInTouchMode=false
            et_processing_fees.isFocusableInTouchMode=false
            et_insurance.isFocusableInTouchMode=false

        }
        else if(ArthanApp.getAppInstance().loginRole.contentEquals("BCM"))
        {
            btn_edit.visibility=View.VISIBLE
            btn_tenure_edit.visibility=View.VISIBLE
            btn_insurance_edit.visibility=View.VISIBLE
            btn_processing_fees_edit.visibility=View.VISIBLE
            btn_rate_of_interest_edit.visibility=View.VISIBLE
            et_loan_amt.isFocusableInTouchMode=true
            et_tenure.isFocusableInTouchMode=true
            et_rate_of_interest.isFocusableInTouchMode=true
            et_processing_fees.isFocusableInTouchMode=true
            et_insurance.isFocusableInTouchMode=true
        }
        else
        {
            btn_edit.visibility=View.GONE
            btn_tenure_edit.visibility=View.GONE
            btn_insurance_edit.visibility=View.GONE
            btn_processing_fees_edit.visibility=View.GONE
            btn_rate_of_interest_edit.visibility=View.GONE
            et_loan_amt.isFocusableInTouchMode=false
            et_tenure.isFocusableInTouchMode=false
            et_rate_of_interest.isFocusableInTouchMode=false
            et_processing_fees.isFocusableInTouchMode=false
            et_insurance.isFocusableInTouchMode=false
        }



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

    override fun screenTitle() = "Review loan details"
}
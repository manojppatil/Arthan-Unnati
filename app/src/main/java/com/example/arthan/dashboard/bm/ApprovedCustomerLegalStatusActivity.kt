package com.example.arthan.dashboard.bm

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.arthan.R
import com.example.arthan.dashboard.bcm.BCMApprovedLegalStatusActivity
import com.example.arthan.dashboard.bcm.BCMApprovedTechDocActivity
import com.example.arthan.dashboard.bcm.BCMDashboardActivity
import com.example.arthan.dashboard.bcm.ExceptionReportActivity
import com.example.arthan.dashboard.rm.CommonApprovedListingActivity
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.global.ArthanApp
import com.example.arthan.model.ApprovedCaseData
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.PendingCustomersActivity
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_approved_customer_legal_status.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ApprovedCustomerLegalStatusActivity : BaseActivity() {

    override fun contentView() = R.layout.activity_approved_customer_legal_status

    override fun onToolbarBackPressed() = onBackPressed()

    private var statusSelected = "approve";
    override fun init() {
        btn_search.visibility = View.GONE
        btn_filter.visibility = View.GONE

        if(ArthanApp.getAppInstance().loginRole=="BCM"){
            btn_moveToBCMQueue.visibility=View.VISIBLE
        }
        screenTitle()
        var data = intent.getSerializableExtra("object") as ApprovedCaseData

        if (data.rcuStatus!=null&&data.rcuStatus.contentEquals("Y")) {
            iv_RCU.setImageResource(R.drawable.checked)

        } else {
            iv_RCU.setImageResource(R.drawable.quit)
        }
        if (data.legalStatus!=null&&data?.legalStatus.toString().contentEquals("Y")) {
            iv_legal.setImageResource(R.drawable.checked)

        } else {
            iv_legal.setImageResource(R.drawable.quit)

        }
        if (data.techStatus!=null&&data.techStatus.toString().contentEquals("Y")) {
            iv_tech.setImageResource(R.drawable.checked)

        } else {
            iv_tech.setImageResource(R.drawable.quit)

        }
        btn_approve.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
            statusSelected = "approve"
        }
        btn_reject.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
            statusSelected = "reject"
        }
        btn_recommendCC.setOnCheckedChangeListener { _, isChecked ->

            if(isChecked)
            statusSelected = "recommend"
        }
        btn_moveToBCMQueue.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
            statusSelected = "Move to My Queue"
        }

        btn_view_rcu_doc.setOnClickListener {


        }
        btn_view_legal_doc.setOnClickListener {

            startActivity(Intent(this,BCMApprovedLegalStatusActivity::class.java).apply {
                putExtra("loanId",data.caseId)
            })
        }

        btn_view_tech_doc.setOnClickListener {

            startActivity(Intent(this,BCMApprovedTechDocActivity::class.java).apply {
                putExtra("loanId",data.caseId)
            })
        }
        exceptionReport.setOnClickListener {
            startActivity(Intent(this,ExceptionReportActivity::class.java).apply {
                putExtra("loanId",data.caseId)
            })
        }




        btn_Submit_data.setOnClickListener {


            var dialog = AlertDialog.Builder(this@ApprovedCustomerLegalStatusActivity)
            var view: View? = this@ApprovedCustomerLegalStatusActivity.layoutInflater.inflate(R.layout.remarks_popup, null)
            dialog.setView(view)
            var et_remarks = view?.findViewById<EditText>(R.id.et_remarks)
            var btn_submit_remark = view?.findViewById<Button>(R.id.btn_submit)
            var btn_cancel = view?.findViewById<Button>(R.id.btn_cancel)

            var alert= dialog.create() as AlertDialog
            alert.show()
            btn_cancel?.setOnClickListener {
                alert.dismiss()
            }
            btn_submit_remark?.setOnClickListener {

                val progressBar = ProgrssLoader(this)
                progressBar.showLoading()
                alert.dismiss()
                CoroutineScope(Dispatchers.IO).launch {
                    var map = HashMap<String, String>()
                    map["loanId"] = data.caseId
                    map["userId"] = ArthanApp.getAppInstance().loginUser
                    map["decision"] = statusSelected
                    map["remarks"] = et_remarks?.text.toString()
                    var response = RetrofitFactory.getApiService().bcmRLTSubmit(map)

                    if (response?.body() != null) {
                        Log.d("bcmRLTSubmit", response.body().toString())
                        /*     {
                        "apiCode":"200",
                        "apiDesc":"Success",
                        "eligibility":"Y"
                                }

                         */
                        if(response.body()!!.eligibility=="N")
                        {

                            withContext(Dispatchers.Main)
                            {
                                progressBar.dismmissLoading()
                                Toast.makeText(this@ApprovedCustomerLegalStatusActivity,response.body()!!.apiDesc,Toast.LENGTH_LONG).show()
                            }
                        }else {
                            withContext(Dispatchers.Main) {
                                progressBar.dismmissLoading()
                                if ((data.rcuStatus.equals(
                                        "Y",
                                        ignoreCase = true
                                    ) && data.techStatus.equals(
                                        "Y",
                                        ignoreCase = true
                                    ) && data.legalStatus.equals("Y", ignoreCase = true))
                                    && (data.rcuReport.toLowerCase().equals(
                                        "positive",
                                        ignoreCase = true
                                    ) && data.legalReport.toLowerCase().equals(
                                        "positive",
                                        ignoreCase = true
                                    )
                                            && data.techReport.toLowerCase().contentEquals("positive"))
                                ) {
                                    when (statusSelected) {
                                        "approve" -> {
                                            startActivity(
                                                Intent(
                                                    this@ApprovedCustomerLegalStatusActivity,
                                                    LPCActivity::class.java
                                                ).apply {
                                                    putExtra(
                                                        "FROM",
                                                        ArthanApp.getAppInstance().loginRole
                                                    )
                                                    putExtra("Name", intent.getStringExtra("Name"))
                                                    putExtra(
                                                        "object",
                                                        intent.getSerializableExtra("object")
                                                    )
                                                })
                                        }
                                        "reject", "recommend", "Move to My Queue" -> {
                                            startActivity(
                                                Intent(
                                                    this@ApprovedCustomerLegalStatusActivity,
                                                    CommonApprovedListingActivity::class.java
                                                ).apply {
                                                    putExtra(
                                                        "FROM",
                                                        ArthanApp.getAppInstance().loginRole
                                                    )
                                                    putExtra("Name", intent.getStringExtra("Name"))
                                                    putExtra(
                                                        "object",
                                                        intent.getSerializableExtra("object")
                                                    )
                                                })

                                        }
                                    }

                                } else if ((data.rcuStatus.equals(
                                        "n",
                                        ignoreCase = true
                                    ) && data.techStatus.equals(
                                        "Y",
                                        ignoreCase = true
                                    ) && data.legalStatus.equals("y", ignoreCase = true))
                                    && (data.legalReport.toLowerCase().contentEquals("positive") && data.techReport.toLowerCase().contentEquals(
                                        "positive"
                                    ))
                                ) {
                                    when (statusSelected) {
                                        "approve" -> {
                                            startActivity(
                                                Intent(
                                                    this@ApprovedCustomerLegalStatusActivity,
                                                    LPCActivity::class.java
                                                ).apply {
                                                    putExtra(
                                                        "FROM",
                                                        ArthanApp.getAppInstance().loginRole
                                                    )
                                                    putExtra("Name", intent.getStringExtra("Name"))
                                                    putExtra(
                                                        "object",
                                                        intent.getSerializableExtra("object")
                                                    )
                                                })
                                        }
                                        "reject", "recommend", "Move to My Queue" -> {
                                            startActivity(
                                                Intent(
                                                    this@ApprovedCustomerLegalStatusActivity,
                                                    CommonApprovedListingActivity::class.java
                                                ).apply {
                                                    putExtra(
                                                        "FROM",
                                                        ArthanApp.getAppInstance().loginRole
                                                    )
                                                    putExtra("Name", intent.getStringExtra("Name"))
                                                    putExtra(
                                                        "object",
                                                        intent.getSerializableExtra("object")
                                                    )
                                                })

                                        }
                                    }
                                } else {
                                    startActivity(
                                        Intent(
                                            this@ApprovedCustomerLegalStatusActivity,
                                            PendingCustomersActivity::class.java
                                        ).apply {
                                            putExtra("FROM", ArthanApp.getAppInstance().loginRole)

                                        })
                                }
                            }
                        }
                    }else
                    {
                        progressBar.dismmissLoading()
                    }
                }
            }



        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {


        menuInflater.inflate(R.menu.more, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.homeMenu -> {
                finish()
                if(ArthanApp.getAppInstance().loginRole=="RM")
                {
                    startActivity(Intent(this, RMDashboardActivity::class.java))
                }else if(ArthanApp.getAppInstance().loginRole=="BCM")
                {
                    startActivity(Intent(this, BCMDashboardActivity::class.java))

                }else if(ArthanApp.getAppInstance().loginRole=="BM")
                {
                    startActivity(Intent(this,BMDashboardActivity::class.java))

                }
            }
            R.id.logoutMenu -> {
                finish()
                startActivity(Intent(this, SplashActivity::class.java))
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun screenTitle() = intent.getStringExtra("Name")!!
}
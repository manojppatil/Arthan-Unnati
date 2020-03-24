package com.example.arthan.dashboard.bm

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.annotation.NonNull
import com.example.arthan.R
import com.example.arthan.model.ApprovedCaseData
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.PendingCustomersActivity
import kotlinx.android.synthetic.main.activity_approved_customer_legal_status.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*
import org.jetbrains.annotations.NotNull

class ApprovedCustomerLegalStatusActivity: BaseActivity() {
    
    override fun contentView()= R.layout.activity_approved_customer_legal_status

    override fun onToolbarBackPressed()  = onBackPressed()

    private var statusSelected="";
    override fun init() {
        btn_search.visibility = View.GONE
        btn_filter.visibility = View.GONE


        screenTitle()
        var data=intent.getSerializableExtra("object") as ApprovedCaseData

        if(data.rcuStatus.contentEquals("Y"))
        {
            iv_RCU.setImageResource(R.drawable.checked)

        }
        else
        {
            iv_RCU.setImageResource(R.drawable.quit)
        }
        if(data.legalStatus.toString().contentEquals("Y"))
        {
            iv_legal.setImageResource(R.drawable.checked)

        }
        else
        {
            iv_legal.setImageResource(R.drawable.quit)

        }
        if(data.techStatus.toString().contentEquals("Y"))
        {
            iv_tech.setImageResource(R.drawable.checked)

        }else
        {
            iv_tech.setImageResource(R.drawable.quit)

        }
       btn_approve.setOnCheckedChangeListener { buttonView, isChecked ->
           statusSelected="approve"
       }
        btn_reject.setOnCheckedChangeListener { buttonView, isChecked ->
            statusSelected="reject"
        }
        btn_recommendCC.setOnCheckedChangeListener { buttonView, isChecked ->
            statusSelected="recommend"
        }



        btn_Submit.setOnClickListener {

            if((data.rcuStatus.contentEquals("Y")&&data.techStatus.contentEquals("Y")&&data.legalStatus.contentEquals("y"))
                &&(data.rcuReport.toLowerCase().contentEquals("positive")&&data.legalReport.toLowerCase().contentEquals("positive")
                        &&data.techReport.toLowerCase().contentEquals("positive")))
            {
                startActivity(
                    Intent(
                        this@ApprovedCustomerLegalStatusActivity,
                        ApprovedCustomerReviewActivity::class.java
                    ).apply {
                        putExtra("FROM", intent.getStringExtra("FROM"))
                        putExtra("Name", intent.getStringExtra("Name"))
                        putExtra("object",intent.getSerializableExtra("object"))
                    })
            }
            else if((data.rcuStatus.contentEquals("n")&&data.techStatus.contentEquals("Y")&&data.legalStatus.contentEquals("y"))
                &&(data.legalReport.toLowerCase().contentEquals("positive")&&data.techReport.toLowerCase().contentEquals("positive")))
            {
                startActivity(
                    Intent(
                        this@ApprovedCustomerLegalStatusActivity,
                        ApprovedCustomerReviewActivity::class.java
                    ).apply {
                        putExtra("FROM", intent.getStringExtra("FROM"))
                        putExtra("Name", intent.getStringExtra("Name"))
                        putExtra("object",intent.getSerializableExtra("object"))
                    })
            }
            else
            {
                startActivity(
                    Intent(
                        this@ApprovedCustomerLegalStatusActivity,
                        PendingCustomersActivity::class.java
                    ).apply {
                        putExtra("FROM", intent.getStringExtra("FROM"))

                    })
            }

        }
    }

    override fun screenTitle()= intent.getStringExtra("Name")
}
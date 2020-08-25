package com.example.arthan.dashboard.bcm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.arthan.R
import com.example.arthan.lead.AddLeadStep2Activity
import com.example.arthan.lead.PersonalInformationActivity
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_b_c_m_approved_add_co_applicant.*

class BCMApprovedAddCoApplicant : BaseActivity() {
    override fun contentView(): Int {
        return R.layout.activity_b_c_m_approved_add_co_applicant
    }

    override fun init() {

        kyc.setOnClickListener {
            startActivity(Intent(this,AddLeadStep2Activity::class.java).apply {
                putExtra("loanId",intent.getStringExtra("loanId"))
                putExtra("task","Add-CoApplicant")
                putExtra("type","CA")
            })
        }
        personal.setOnClickListener {

            startActivity(Intent(this,PersonalInformationActivity::class.java).apply {
                putExtra("loanId",intent.getStringExtra("loanId"))
                putExtra("task",intent.getStringExtra("Add-CoApplicant"))
                putExtra("type","CA")
            })
        }
        collateral.setOnClickListener {

            startActivity(Intent(this,BCMApprovedCollateral::class.java).apply {
                putExtra("loanId",intent.getStringExtra("loanId"))
                putExtra("task","Add-CoApplicant")
                putExtra("loanType","immovable")

            })

        }

    }

    override fun onToolbarBackPressed() {
        finish()
    }

    override fun screenTitle(): String {
        return "Add Co-Applicant"
    }
}

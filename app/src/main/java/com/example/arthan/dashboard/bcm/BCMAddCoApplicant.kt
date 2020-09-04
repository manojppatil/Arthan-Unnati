package com.example.arthan.dashboard.bcm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.arthan.R
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_b_c_m_add_co_applicant.*

class BCMAddCoApplicant : BaseActivity() {
    override fun contentView(): Int {
        return R.layout.activity_b_c_m_add_co_applicant
    }

    override fun init() {
        coApplicantAdd.setOnClickListener {

            startActivity(Intent(this,BCMApprovedCollateral::class.java).apply {
                putExtra("loanId",intent.getStringExtra("loanId"))
                putExtra("task","ReAssigned")
            })
        }

    }

    override fun onToolbarBackPressed() {
       finish()
    }

    override fun screenTitle(): String {
        return "BCM Legal"
    }
}

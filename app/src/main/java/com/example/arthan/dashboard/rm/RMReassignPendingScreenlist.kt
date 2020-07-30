package com.example.arthan.dashboard.rm

import android.content.Intent
import android.view.View
import com.example.arthan.R
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.reassign_pending_screen_fragment.*

class RMReassignPendingScreenlist : BaseActivity() {
    override fun contentView(): Int {

        return R.layout.reassign_pending_screen_fragment
    }

    override fun init() {
        if (intent.getBooleanExtra("Documents", false)) {
            btn_documents_rm.visibility = View.VISIBLE
        } else {
            btn_documents_rm.visibility = View.GONE

        }
        if (intent.getBooleanExtra("Business", false)) {
            btn_business_rm.visibility = View.VISIBLE
        } else {
            btn_business_rm.visibility = View.GONE

        }
        if (intent.getBooleanExtra("Income", false)) {
            btn_income_rm.visibility = View.VISIBLE

        } else {
            btn_income_rm.visibility = View.GONE

        }
        if (intent.getBooleanExtra("Others", false)) {
            btn_others_rm.visibility = View.VISIBLE
        } else {
            btn_others_rm.visibility = View.GONE

        }
        if (intent.getBooleanExtra("loan", false)) {
            btn_others_rm.visibility = View.VISIBLE
        } else {
            btn_others_rm.visibility = View.GONE

        }
        if (intent.getBooleanExtra("personal", false)) {
            btn_others_rm.visibility = View.VISIBLE
        } else {
            btn_others_rm.visibility = View.GONE

        }
        if (intent.getBooleanExtra("kyc", false)) {
            btn_others_rm.visibility = View.VISIBLE
        } else {
            btn_others_rm.visibility = View.GONE

        }
        btn_others_rm.setOnClickListener {
            startActivity(Intent(this, ReUsableFragmentSpace::class.java).apply {
                putExtra("task", "RM_AssignList")
                putExtra("screen", "Others")
                putExtra("loanId", intent.getStringExtra("loanId"))

            })

        }
        btn_income_rm.setOnClickListener {
            startActivity(Intent(this, ReUsableFragmentSpace::class.java).apply {
                putExtra("task", "RM_AssignList")
                putExtra("screen", "Income")
                putExtra("loanId", intent.getStringExtra("loanId"))


            })


        }
        btn_business_rm.setOnClickListener {
            startActivity(Intent(this, ReUsableFragmentSpace::class.java).apply {
                putExtra("task", "RM_AssignList")
                putExtra("screen", "Business")
                putExtra("loanId", intent.getStringExtra("loanId"))


            })

        }
        btn_documents_rm.setOnClickListener {
            startActivity(Intent(this, ReUsableFragmentSpace::class.java).apply {
                putExtra("task", "RM_AssignList")
                putExtra("screen", "Documents")
                putExtra("loanId", intent.getStringExtra("loanId"))


            })

        }


    }

    override fun onToolbarBackPressed() {
        finish()
    }


    override fun screenTitle(): String {
        return "RMReassign"
    }

}
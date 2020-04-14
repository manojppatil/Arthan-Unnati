package com.example.arthan.dashboard.bm

import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import com.example.arthan.R
import com.example.arthan.dashboard.rm.*
import com.example.arthan.views.activities.PendingCustomersActivity
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.NotificationActivity
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_bm_dashboard.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*

class BMDashboardActivity : BaseActivity(), OnClickListener {

    override fun contentView() = R.layout.activity_bm_dashboard

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        cv_rm_review.setOnClickListener(this)
        cv_leads.setOnClickListener(this)
        cv_my_queue.setOnClickListener(this)
        cv_approved.setOnClickListener(this)
        cv_inprogress.setOnClickListener(this)
        cv_rejected.setOnClickListener(this)
        cv_reassign_to.setOnClickListener(this)
        cv_reassigned_by.setOnClickListener(this)
        cv_disbursed.setOnClickListener(this)

        img_notification.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }
        logoutIv.visibility=View.VISIBLE
        logoutIv.setOnClickListener {
            startActivity(Intent(this@BMDashboardActivity, SplashActivity::class.java))
            finish()
        }
    }

    override fun screenTitle() = ""

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.cv_rm_review -> RMInBranchListingActivity.startMe(
                this@BMDashboardActivity,
                BranchLaunchType.BM
            )
            R.id.cv_leads -> startActivity(
                Intent(
                    this@BMDashboardActivity,
                    RMLeadListingActivity::class.java
                ).apply {
                    putExtra("FROM", "BM")
                })
            R.id.cv_my_queue -> startActivity(
                Intent(
                    this@BMDashboardActivity,
                    PendingCustomersActivity::class.java
                ).apply {
                    putExtra("FROM", "BM")
                })
            R.id.cv_approved -> startActivity(
                Intent(
                    this@BMDashboardActivity,
                    RMApprovedListingActivity::class.java
                ).apply {
                    putExtra("FROM", "BM")
                })
            R.id.cv_inprogress -> startActivity(
                Intent(
                    this@BMDashboardActivity,
                    InProgressListingActivity::class.java
                )
            )
            R.id.cv_rejected -> startActivity(
                Intent(
                    this@BMDashboardActivity,
                    RMRejectedListingActivity::class.java
                ).apply {
                    putExtra("FROM", "BM")
                })
            R.id.cv_reassign_to -> startActivity(
                Intent(
                    this@BMDashboardActivity,
                    RMReAssignListingActivity::class.java
                ).apply {
                    putExtra("FROM", "REASSIGN-TO")
                })
            R.id.cv_reassigned_by -> startActivity(
                Intent(
                    this@BMDashboardActivity,
                    RMReAssignListingActivity::class.java
                ).apply {
                    putExtra("FROM", "REASSIGN-BY")
                })
            R.id.cv_disbursed -> startActivity(
                Intent(
                    this@BMDashboardActivity,
                    RMDisbursedListingActivity::class.java
                )
            )
        }
    }
}
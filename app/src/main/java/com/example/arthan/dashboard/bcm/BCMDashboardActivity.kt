package com.example.arthan.dashboard.bcm

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.example.arthan.R
import com.example.arthan.dashboard.bm.InProgressListingActivity
import com.example.arthan.dashboard.rm.RMApprovedListingActivity
import com.example.arthan.dashboard.rm.RMDisbursedListingActivity
import com.example.arthan.dashboard.rm.RMReAssignListingActivity
import com.example.arthan.dashboard.rm.RMRejectedListingActivity
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.NotificationActivity
import com.example.arthan.views.activities.PendingCustomersActivity
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_bcm_dashboard.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*

class BCMDashboardActivity : BaseActivity(), View.OnClickListener {

    override fun contentView() = R.layout.activity_bcm_dashboard

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        layout_toolbar.visibility = View.VISIBLE
        txt_title.visibility=View.GONE
        btn_search.visibility=View.GONE
        btn_filter.visibility=View.GONE
        logoutIv.visibility=View.VISIBLE

       setSupportActionBar(layout_toolbar as Toolbar?)
        img_notification.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        cv_my_queue.setOnClickListener(this)
        cv_approved.setOnClickListener(this)
        cv_rejected.setOnClickListener(this)
        cv_inprogress.setOnClickListener(this)
        cv_reassign_to.setOnClickListener(this)
        cv_reassigned_by.setOnClickListener(this)
        cv_disbursed.setOnClickListener(this)
        logoutIv.visibility=View.GONE
        logoutIv.setOnClickListener {
            startActivity(Intent(this@BCMDashboardActivity, SplashActivity::class.java))
            finish()
        }

    }

    override fun screenTitle() = ""

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.cv_my_queue -> startActivity(
                Intent(
                    this@BCMDashboardActivity,
                    PendingCustomersActivity::class.java
                ).apply {
                    putExtra("FROM", "BCM")
                })
            R.id.cv_approved -> startActivity(
                Intent(
                    this@BCMDashboardActivity,
                    RMApprovedListingActivity::class.java
                ).apply {
                    putExtra("FROM", "BCM")
                })
            R.id.cv_inprogress -> startActivity(
                Intent(
                    this@BCMDashboardActivity,
                    InProgressListingActivity::class.java
                )
            )
            R.id.cv_rejected -> startActivity(
                Intent(
                    this@BCMDashboardActivity,
                    RMRejectedListingActivity::class.java
                ).apply {
                    putExtra("FROM", "BCM")
                })
            R.id.cv_reassign_to -> startActivity(
                Intent(
                    this@BCMDashboardActivity,
                    RMReAssignListingActivity::class.java
                ).apply {
                    putExtra("FROM", "REASSIGN-TO")
                })
            R.id.cv_reassigned_by -> startActivity(
                Intent(
                    this@BCMDashboardActivity,
                    RMReAssignListingActivity::class.java
                ).apply {
                    putExtra("FROM", "REASSIGN-BY")
                })
            R.id.cv_disbursed -> startActivity(
                Intent(
                    this@BCMDashboardActivity,
                    RMDisbursedListingActivity::class.java
                )
            )
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
}
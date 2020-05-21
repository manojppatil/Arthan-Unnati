package com.example.arthan.dashboard.bm

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.widget.Toolbar
import com.example.arthan.R
import com.example.arthan.dashboard.rm.*
import com.example.arthan.global.ArthanApp
import com.example.arthan.model.BMDashboardResponseData
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.PendingCustomersActivity
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.NotificationActivity
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_bm_dashboard.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BMDashboardActivity : BaseActivity(), OnClickListener {

    override fun contentView() = R.layout.activity_bm_dashboard

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {
        setSupportActionBar(layout_toolbar as Toolbar?)


        getBMStatsData()
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
        logoutIv.visibility=View.GONE
        logoutIv.setOnClickListener {
            startActivity(Intent(this@BMDashboardActivity, SplashActivity::class.java))
            finish()
        }
    }

    private fun getBMStatsData() {
        val progressBar: ProgrssLoader? =ProgrssLoader(this)
        progressBar?.showLoading()
        CoroutineScope(Dispatchers.IO).launch {

            var map=HashMap<String,String>()
            map["userId"] = ArthanApp.getAppInstance().loginUser
            var  response=RetrofitFactory.getApiService().getBMDashboard(map)
            if(response!=null)
            {
                withContext(Dispatchers.Main)
                {
                    progressBar?.dismmissLoading()
                    setDataToLabels (response.body())
                }
            }else{
                withContext(Dispatchers.Main)
                {
                    progressBar?.dismmissLoading()
                }
            }
        }
    }

    private fun setDataToLabels(body: BMDashboardResponseData?) {

        txt_rm_review_count.text=body?.rmReview?.count
        txt_lead_count.text=body?.leads?.count
        txt_cv_my_queue_count.text=body?.myQueue?.count
        txt_approved_count.text=body?.approved?.count
//        txt_inprogress_count.text=body?.approved?.count
        txt_rejected_count.text=body?.rejected?.count
        txt_reassigned_to_count.text=body?.reassignTo?.count
        txt_reassigned_by_count.text=body?.reassignBy?.count
        txt_rm_review_count_tot.text=body?.rmReview?.count

        txt_lead_count_tot.text=body?.leads?.total
        txt_cv_my_queue_count_tot.text=body?.myQueue?.total
        txt_approved_count_tot.text=body?.approved?.total
//        txt_inprogress_count.text=body?.approved?.count
        txt_rejected_count_tot.text=body?.rejected?.total
        txt_reassigned_to_count_tot.text=body?.reassignTo?.total
        txt_reassigned_by_count_tot.text=body?.reassignBy?.total
//        txt_disbursed_count.text=body?.rmReview?.count

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
                    CommonApprovedListingActivity::class.java
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
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {


        menuInflater.inflate(R.menu.more,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.logoutMenu->
            {
                finish()
                startActivity(Intent(this,SplashActivity::class.java))
            }

        }
        return super.onOptionsItemSelected(item)
    }
}
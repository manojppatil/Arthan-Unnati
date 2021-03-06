package com.example.arthan.dashboard.bcm

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.example.arthan.R
import com.example.arthan.dashboard.bm.BMDashboardActivity
import com.example.arthan.dashboard.bm.InProgressListingActivity
import com.example.arthan.dashboard.rm.*
import com.example.arthan.global.ArthanApp
import com.example.arthan.model.BMDashboardResponseData
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.NotificationActivity
import com.example.arthan.views.activities.PendingCustomersActivity
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_bcm_dashboard.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BCMDashboardActivity : BaseActivity(), View.OnClickListener {

    override fun contentView() = R.layout.activity_bcm_dashboard

    override fun onToolbarBackPressed() = onBackPressed()

    lateinit var responseData:BMDashboardResponseData
    override fun init() {

        ArthanApp.getAppInstance().currentCustomerId=null
        layout_toolbar.visibility = View.VISIBLE
        txt_title.visibility=View.GONE
        btn_search.visibility=View.GONE
        btn_filter.visibility=View.GONE
        logoutIv.visibility=View.VISIBLE
        getBCMStatsData()
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
                    putExtra("count", responseData.myQueue.count)
                })
            R.id.cv_approved -> startActivity(
                Intent(
                    this@BCMDashboardActivity,
                    CommonApprovedListingActivity::class.java
                ).apply {
                    putExtra("FROM", "BCM")
                })
            R.id.cv_inprogress ->

                startActivity(Intent(
                    this,
                    RMInProgressActivity::class.java
                ))/*startActivity(
                Intent(
                    this@BCMDashboardActivity,
                    InProgressListingActivity::class.java
                )
            )*/
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
//                    RMReAssignListingActivity::class.java
                    BCMReAssignedActivity::class.java
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
    private fun getBCMStatsData() {
        val progressBar: ProgrssLoader? = ProgrssLoader(this)
        progressBar?.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            var map=HashMap<String,String>()
            map["userId"] = ArthanApp.getAppInstance().loginUser

            var  response= RetrofitFactory.getApiService().getBCMDashboard(map)
            if(response!=null)
            {
                withContext(Dispatchers.Main)
                {
                    if(response.body()!= null) {
                        responseData = response.body()!!
                    }
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

        txt_bm_name.text="Hello "+body?.empName

        txt_cv_my_queue_count.text=body?.myQueue?.count
        txt_approved_count.text=body?.approved?.count
        txt_rejected_count.text=body?.rejected?.count
        txt_reassigned_to_count.text=body?.reassignTo?.count
        txt_reassigned_by_count.text=body?.reassignBy?.count
        txt_inprogress_count.text=body?.inProgress?.count



        txt_cv_my_queue_count_tot.text=body?.myQueue?.total
        txt_approved_count_tot.text=body?.approved?.total
        txt_rejected_count_tot.text=body?.rejected?.total
        txt_reassigned_to_count_tot.text=body?.reassignTo?.total
        txt_reassigned_by_count_tot.text=body?.reassignTo?.total
        txt_inprogress_count_tot.text=body?.inProgress?.total

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.homeMenu->{
                finish()
                if(ArthanApp.getAppInstance().loginRole=="RM")
                {
                   startActivity(Intent(this,RMDashboardActivity::class.java))
                }else if(ArthanApp.getAppInstance().loginRole=="BCM")
                {
                    startActivity(Intent(this,BCMDashboardActivity::class.java))

                }else if(ArthanApp.getAppInstance().loginRole=="BM")
                {
                    startActivity(Intent(this,BMDashboardActivity::class.java))

                }

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
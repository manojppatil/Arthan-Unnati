package com.example.arthan.dashboard.bm

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.arthan.R
import com.example.arthan.global.STATUS
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.SplashActivity
import com.example.arthan.views.activities.SubmitFinalReportActivity
import kotlinx.android.synthetic.main.activity_bm_screeening_report.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*

class BMScreeningReportActivity: BaseActivity(),View.OnClickListener {

    override fun contentView()= R.layout.activity_bm_screeening_report

    override fun onToolbarBackPressed() = onBackPressed()

    private var loanId:String?=null
    private var custId:String?=null
    override fun init() {
        txt_recommend_bcm.setOnClickListener(this)
        txt_recommend_rm.setOnClickListener(this)
        txt_reject.setOnClickListener(this)
        txt_approve.setOnClickListener(this)

        btn_search.visibility= View.GONE
        btn_filter.visibility= View.GONE

        txt_industry.text=intent.getStringExtra("indSeg")
        txt_date.text=intent.getStringExtra("loginDate")
        loanId=intent.getStringExtra("loanId")
        txt_amount.text=intent.getStringExtra("loanAmt")
        txt_customer_name.text=intent.getStringExtra("cname")
         custId=intent.getStringExtra("custId")

        if(intent.getStringExtra("FROM")=="BM")
        {

            txt_recommend_bcm.text="Recommend to BCM"
            txt_approve.visibility=View.GONE


        }else if(intent.getStringExtra("FROM")=="BCM")
        {
            txt_recommend_bcm.text="Recommend to CC"

        }else
        {
            txt_approve.visibility=View.GONE

        }

    }

    override fun screenTitle()= "Final Screening Report"

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.txt_recommend_bcm -> {
                startActivity(Intent(this,SubmitFinalReportActivity::class.java).apply {
                    putExtra(STATUS,txt_recommend_bcm.text.toString())
                    putExtra("indSeg",intent.getStringExtra("indSeg"))
                    putExtra("loginDate",intent.getStringExtra("loginDate"))
                    putExtra("loanId",loanId)
                    putExtra("custId",custId)
                    putExtra("FROM",intent.getStringExtra("FROM"))

                })
                finish()

            }
            R.id.txt_recommend_rm -> {
                startActivity(Intent(this,SubmitFinalReportActivity::class.java).apply {
                    putExtra(STATUS,"Recommend To RM")
                    putExtra("indSeg",intent.getStringExtra("indSeg"))
                    putExtra("loginDate",intent.getStringExtra("loginDate"))
                    putExtra("loanId",loanId)
                    putExtra("custId",custId)
                    putExtra("FROM",intent.getStringExtra("FROM"))

                })
                finish()

            }
            R.id.txt_reject -> {
                startActivity(Intent(this,SubmitFinalReportActivity::class.java).apply {
                    putExtra(STATUS,"Reject")
                    putExtra("indSeg",intent.getStringExtra("indSeg"))
                    putExtra("loginDate",intent.getStringExtra("loginDate"))
                    putExtra("loanId",loanId)
                    putExtra("custId",custId)
                    putExtra("FROM",intent.getStringExtra("FROM"))

                })
                finish()

            }

            R.id.txt_approve -> {
                startActivity(Intent(this,SubmitFinalReportActivity::class.java).apply {
                    putExtra(STATUS,"Approve")
                    putExtra("indSeg",intent.getStringExtra("indSeg"))
                    putExtra("loginDate",intent.getStringExtra("loginDate"))
                    putExtra("loanId",loanId)
                    putExtra("custId",custId)
                    putExtra("FROM",intent.getStringExtra("FROM"))


                })
                finish()
            }
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
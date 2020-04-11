package com.example.arthan.dashboard.bm

import android.content.Intent
import android.view.View
import com.example.arthan.R
import com.example.arthan.global.STATUS
import com.example.arthan.views.activities.BaseActivity
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

        btn_search.visibility= View.GONE
        btn_filter.visibility= View.GONE

        txt_industry.text=intent.getStringExtra("indSeg")
        txt_date.text=intent.getStringExtra("loginDate")
        loanId=intent.getStringExtra("loanId")
        txt_amount.text=intent.getStringExtra("loanAmt")
        txt_customer_name.text=intent.getStringExtra("cname")
         custId=intent.getStringExtra("custId")


    }

    override fun screenTitle()= "Final Screening Report"

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.txt_recommend_bcm -> {
                startActivity(Intent(this,SubmitFinalReportActivity::class.java).apply {
                    putExtra(STATUS,"Recommend To AA")
                    putExtra("indSeg",intent.getStringExtra("indSeg"))
                    putExtra("loginDate",intent.getStringExtra("loginDate"))
                    putExtra("loanId",loanId)
                    putExtra("custId",custId)

                })
            }
            R.id.txt_recommend_rm -> {
                startActivity(Intent(this,SubmitFinalReportActivity::class.java).apply {
                    putExtra(STATUS,"Recommend To RM")
                    putExtra("indSeg",intent.getStringExtra("indSeg"))
                    putExtra("loginDate",intent.getStringExtra("loginDate"))
                    putExtra("loanId",loanId)
                    putExtra("custId",custId)

                })
            }
            R.id.txt_reject -> {
                startActivity(Intent(this,SubmitFinalReportActivity::class.java).apply {
                    putExtra(STATUS,"Reject")
                    putExtra("indSeg",intent.getStringExtra("indSeg"))
                    putExtra("loginDate",intent.getStringExtra("loginDate"))
                    putExtra("loanId",loanId)
                    putExtra("custId",custId)

                })
            }
        }
    }
}
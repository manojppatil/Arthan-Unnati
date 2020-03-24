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

    override fun init() {
        txt_recommend_bcm.setOnClickListener(this)
        txt_recommend_rm.setOnClickListener(this)
        txt_reject.setOnClickListener(this)

        btn_search.visibility= View.GONE
        btn_filter.visibility= View.GONE

    }

    override fun screenTitle()= "Final Screening Report"

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.txt_recommend_bcm -> {
                startActivity(Intent(this,SubmitFinalReportActivity::class.java).apply {
                    putExtra(STATUS,"Recommend To BCM")
                })
            }
            R.id.txt_recommend_rm -> {
                startActivity(Intent(this,SubmitFinalReportActivity::class.java).apply {
                    putExtra(STATUS,"Recommend To RM")
                })
            }
            R.id.txt_reject -> {
                startActivity(Intent(this,SubmitFinalReportActivity::class.java).apply {
                    putExtra(STATUS,"Reject")
                })
            }
        }
    }
}
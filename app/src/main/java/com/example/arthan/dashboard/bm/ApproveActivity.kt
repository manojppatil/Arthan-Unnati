package com.example.arthan.dashboard.bm

import android.content.Intent
import android.view.View
import com.example.arthan.R
import com.example.arthan.dashboard.bcm.BCMDashboardActivity
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activtiy_approve.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*

class ApproveActivity: BaseActivity() {

    override fun contentView()= R.layout.activtiy_approve

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        btn_search.visibility= View.GONE
        btn_filter.visibility= View.GONE

        btn_submit.setOnClickListener {

            val from= intent.getStringExtra("FROM")

            if(from == "BM") {
                val intent = Intent(this, BMDashboardActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else if(from == "BCM") {
                val intent = Intent(this, BCMDashboardActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

    }

    override fun screenTitle()= "Documents"

}
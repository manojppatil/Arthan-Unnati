package com.example.arthan.dashboard.bm

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.arthan.R
import com.example.arthan.dashboard.bcm.BCMDashboardActivity
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activtiy_approve.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*

class PostSanctionDocActivity: BaseActivity() {

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